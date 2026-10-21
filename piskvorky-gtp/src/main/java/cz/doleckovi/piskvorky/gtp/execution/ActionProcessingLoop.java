package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.trait.*;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Action-execution stage of the pipeline: runs {@link Action}s with the session
 * {@link Context} and hands every produced {@link Result} to the downstream dispatch
 * stage, trait-copying the action's markers onto the result before the submission.
 * The stage is a queue plus one dedicated worker thread: actions are executed in
 * submission order, each future settling when its result has been handed off.
 * <p>
 * This stage is where {@link Sync} and {@link Canceling} are interpreted - commands
 * execute here and only their bodies can react to winding down.
 *
 * <h2>Termination</h2>
 * <p>The only way this stage ends gracefully is an action carrying {@link Shutdown}
 * flowing through the queue. Terminal actions drain - everything accepted before the
 * marker executes first. Following submissions return {@link RejectedExecutionException}
 * and accepted-but-never-run work is cancelled by the exiting worker's closing block.
 * Nothing else stops the worker: it parks while the queue is empty and an interrupt
 * flag left behind by a body is consumed as belonging to no action - the stage never
 * interrupts its worker and no host ends it any other way.</p>
 * <p>{@link Shutdown} is <em>not</em> {@code Sync}: a terminal action's result travels
 * downstream without the submitter or this worker waiting, and the terminal marker
 * cascades exactly like it ends this stage - the completed end is observed through the
 * stage's termination hook.</p>
 * <p>A downstream that refuses our results ({@link RejectedExecutionException} escaping
 * the hand-off) ends this stage too - no stage outlives its sink. An {@code Error}
 * escaping a body or the hand-off ends it as well: the JVM is considered unrecoverable.</p>
 *
 * <h2>Sync discipline</h2>
 * <p>{@link #submit} of a {@link Sync} action blocks the submitting thread until the action
 * has executed <em>and</em> its result has been dispatched (GTP spec &sect;6.3.1):
 * the hand-off waits for the dispatched {@code Sync} result's future, so the wait
 * chains through the hand-off. A {@code Sync} action must never be submitted from this
 * stage's own worker thread (it would wait for itself).</p>
 *
 * <h2>Canceling sweep</h2>
 * <p>Through the {@link #accepted}, {@link #windowOpened} and {@link #windowClosed} points -
 * all run while the stage lock is held - an accepted {@link Canceling} action asks the
 * currently running {@link Cancelable} action (and every later one that starts before the
 * carrier itself finishes) to wind down. The sweep acts only on the action provably inside
 * its body window, so an about-to-finish {@link Cancelable} and an accepted
 * {@link Canceling} can never interleave into a double-swept or missed victim; winding
 * down is fully cooperative and this stage never interrupts its worker.
 * Results carrying {@code Canceling} (trait-copied here from their actions) are inert
 * at the dispatch stage, which does not interpret the marker.</p>
 *
 * <h2>Failures</h2>
 * <p>Any {@link Throwable} thrown by a body is answered by {@link #handleException}: a
 * {@code RejectedExecutionException} (a dead downstream) and an {@code Error} are
 * rethrown unchanged - the command was not answered and the stage must not keep running -
 * anything else becomes a trait-copied {@link Result.Failure} of the action's message,
 * delivered downstream and settling the future <em>normally</em>; the stage survives.
 * A refused submission settles its own future exceptionally and throws - work that never
 * entered the queue is never delivered.</p>
 *
 * <p>{@code submit} settles each action's future with the dispatched result, so callers
 * (scenario harnesses, TODO.md [11]) can observe outcomes without a wire at all -
 * pairing the stage with a discarding downstream keeps it usable standalone.</p>
 */
public final class ActionProcessingLoop implements ExecutionLoop<Action, Result> {

	private record QueueItem(Action action, CompletableFuture<Result> future) { }

	private static final Logger LOG = Logger.getLogger(ActionProcessingLoop.class.getName());

	private final Context context;
	private final ExecutionLoop<Result, Result> downstream;
	private final Consumer<? super ExecutionLoop<Action, Result>> onTermination;
	private final Thread worker;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition taskAvailable = lock.newCondition();

	private boolean shutdownForwarded = false; // Accessed only from worker
	// Internal state guarded by the lock
	private final Queue<QueueItem> queue = new LinkedList<>();
	private boolean accepting = true;
	private Cancelable currentCancellable;
	private Canceling activeCanceling;

	/** Creates the stage and starts its worker thread.
	 * @param threadFactory factory producing the worker (naming is the wiring's choice)
	 * @param context session facet (messages channel, board and game) handed to executed actions
	 * @param downstream dispatch stage receiving every produced result
	 * @param onTermination hook invoked once, on this stage's worker, after it ended
	 */
	public ActionProcessingLoop(ThreadFactory threadFactory, Context context,
			ExecutionLoop<Result, Result> downstream,
			Consumer<? super ExecutionLoop<Action, Result>> onTermination)
	{
		this.context = Objects.requireNonNull(context, "context");
		this.downstream = Objects.requireNonNull(downstream, "downstream");
		this.onTermination = onTermination;
		worker = threadFactory.newThread(this::loop);
		if (worker == null)
			throw new IllegalStateException("ThreadFactory produced no thread: " + threadFactory);
		worker.start();
		LOG.info("Accepting tasks");
	}

	/**
	 * {@inheritDoc}
	 * <p>A {@link Sync} action is fully executed - and its result dispatched - before
	 * this call returns (see <em>Sync discipline</em> in the class documentation).</p>
	 */
	@Override
	public Future<Result> submit(Action task) throws SubmitRejectedException {
		Objects.requireNonNull(task, "task");
		LOG.fine(() -> "Task submitted: " + task);
		CompletableFuture<Result> future;
		lock.lock();
		try {
			if (!accepting)
				throw new SubmitRejectedException("New tasks are not accepted");
			if (task instanceof Shutdown) {
				accepting = false;
				LOG.info("Shutdown accepted, new tasks will be rejected");
			}
			queue.add(new QueueItem(task, future = new CompletableFuture<>()));
			taskAvailable.signal();
			if (task instanceof Canceling canceling)
				accepted(canceling);
		} finally {
			lock.unlock();
		}
		if (task instanceof Sync) try {
			future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			if (e.getCause() instanceof Error error)
				throw error; // Propagate errors
		} catch (CancellationException e) {
			// Ignore cancel here
		}
		return future;
	}

	/** Updates {@link #activeCanceling} and cancels {@link #runningTask}
	 * <p>Runs on the submitting thread while the stage lock is held.</p>
	 * @param canceling Cancelling action addedto queue, but not executed yet
	 */
	private void accepted(Canceling canceling) {
		activeCanceling = canceling;
		LOG.finer(() -> "Active canceling: " + canceling);
		if (currentCancellable != null) try {
			LOG.finer("Canceling currently executed cancelable");
			if (currentCancellable.cancelDuringExecution()) {
				LOG.finest("Interrupting worker thread");
				worker.interrupt();
			}
		} catch (RuntimeException e) {
			LOG.log(Level.WARNING, "Cancelable callback failed", e);
		} finally {
			currentCancellable = null;
		}
	}

	/** Execution loop. */
	private void loop() {
		Throwable throwable = null;
		try {
			while (!shutdownForwarded) {
				var queueItem = takeTask();
				var action = queueItem.action;
				var result = process(action, queueItem.future);
				forward(Traits.copyTraits(action, result != null ? result : Result.IGNORED), queueItem.future);
			}
		} catch (InterruptedException e) {
			LOG.log(Level.WARNING, "Interrupt in action processing loop", e);
			Thread.currentThread().interrupt();
		} catch (SubmitRejectedException e) {
			LOG.log(Level.WARNING, "Result rejected by downstream execution loop", e);
			shutdownForwarded = true;
			throwable = e;
		} catch (Error|RuntimeException e) {
			LOG.log(Level.SEVERE, "Unhandled exception in action processing loop", e);
			throwable = e;
		} finally {
			throwable = executeSafely(throwable,
					"Failed to drain queue", this::drain);
			throwable = executeSafely(throwable,
					"Failed to send Shutdown downstream", this::shutdownDownstream);
			throwable = executeSafely(throwable,
					"Termination callback failure", this::terminationCallback);
			if (throwable instanceof Error error)
				throw error;
		}
	}

	/** Take next task from queue. */
	private QueueItem takeTask() throws InterruptedException {
		lock.lock();
		try {
			while (queue.isEmpty()) {
				LOG.finest("Waiting for task");
				taskAvailable.await(1000, TimeUnit.MILLISECONDS);
			}
			return queue.remove();
		} finally {
			lock.unlock();
		}
	}

	/** Calls {@link Action#execute(Context)} and completes the future.
	 * <p>Calls {@link #interruptBeforeExecution} and
	 * {@link #restoreInterruptAfterExecution}. Future is exceptionally
	 * completed if the method throws.</p>
	 * @param action Action to execute
	 * @param future Future to complete in case of exception
	 * @return Execution result
	 */
	private Result process(Action action, CompletableFuture<Result> future) {
		try {
			LOG.fine(() -> "Processing action: " + action);
			boolean wasInterrupted = interruptBeforeExecution(action);
			try {
				return action.execute(context);
			} finally {
				restoreInterruptAfterExecution(action, wasInterrupted);
			}
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, e, () -> "Failed to execute action: " + action);
			return new Result.Failure(action.id(), e.getMessage());
		} catch (Error e) {
			future.completeExceptionally(e);
			throw e;
		}
	}

	/** Optionally interrupt worker thread before execution.
	 * <p>If {@code action} is cancelable and {@link #activeCanceling} is not
	 * {@code null}, then {@link Cancelable#cancelBeforeExecution()} is called
	 * to give give it chance to interrupt worker before action execution.</p>
	 * @param action Action to be executed
	 * @return Interruption status of worker before this method gets called
	 */
	private boolean interruptBeforeExecution(Action action) {
		boolean wasInterrupted;
		Cancelable toCancel;
		lock.lock();
		try {
			wasInterrupted = Thread.currentThread().isInterrupted(); // Save interrupt state
			if (action instanceof Cancelable cancelable) {
				currentCancellable = cancelable;
				toCancel = (activeCanceling != null) ? cancelable : null;
			} else {
				toCancel = null;
			}
		} finally {
			lock.unlock();
		}
		if (toCancel != null) try {
			LOG.finer("Canceling before execution");
			toCancel.cancelBeforeExecution(); // Can interrupt
		} catch (RuntimeException e) {
			LOG.log(Level.WARNING, "Before execution cancel failed", e);
		}
		if (Thread.interrupted() || wasInterrupted) {
			LOG.finest("Action will execute with interrupted thread");
			Thread.currentThread().interrupt();
		}
		return wasInterrupted;
	}

	/** Updates {@link #activeCanceling} and restores interruption state.
	 * @param action Action which was just executed
	 * @param wasInterrupted {@code true} if worker was interrupted before execution
	 */
	private void restoreInterruptAfterExecution(Action action, boolean wasInterrupted) {
		lock.lock();
		try {
			currentCancellable = null;
			if (activeCanceling == action) {
				activeCanceling = null;
				LOG.finer("Cancelling is no longer active");
			}
			if (wasInterrupted) {
				Thread.currentThread().interrupt();
			} else {
				Thread.interrupted(); // Clears interrupt status
				LOG.finest("Interrupt status cleared");
			}
		} finally {
			lock.unlock();
		}
	}

	/** Forward result to downstream execution loop.
	 * <p>Executes from worker thread. Updates {@link #shutdownForwarded}.</p>
	 * @param action Action used as source of traits
	 * @param result Action result
	 */
	private void forward(Result result, CompletableFuture<Result> future) {
		try {
			if (result instanceof Sync) try {
				LOG.finer("Waiting for downstream result");
				result = downstream.submit(result).get();
			} catch (InterruptedException e) {
				future.completeExceptionally(e);
				Thread.currentThread().interrupt();
			} catch (ExecutionException e) {
				future.completeExceptionally(e.getCause());
				if (e.getCause() instanceof Error error)
					throw error; // Only errors propagate from downstream
			} catch (CancellationException e) {
				future.cancel(false);
			} else {
				downstream.submit(result);
			}
			future.complete(result);
		} catch (Error|RuntimeException e) {
			future.completeExceptionally(e);
			throw e;
		} finally {
			if (result instanceof Shutdown && !shutdownForwarded) {
				shutdownForwarded = true;
				LOG.info("Shutdown forwarded to downstream execution loop");
			}
		}
	}

	private Throwable executeSafely(Throwable throwable, String errorMessage, Runnable runnable) {
		try {
			runnable.run();
		} catch (RuntimeException e) {
			LOG.log(Level.WARNING, errorMessage, e);
			if (throwable != null) {
				throwable.addSuppressed(e);
			} else {
				throwable = e;
			}
		} catch (Error e) {
			// Serious trouble don't event try to log
			if (throwable != null) {
				throwable.addSuppressed(e);
			} else {
				throwable = e;
			}
		}
		return throwable;
	}

	/** Blocks accepting and drains queue cancelling unprocessed tasks.
	 * <p>Executes from worker final block => must not throw.</p>
	 */
	private void drain() {
		lock.lock();
		try {
			accepting = false;
			QueueItem orphaned;
			while ((orphaned = queue.poll()) != null)
				orphaned.future().cancel(false);
		} finally {
			lock.unlock();
		}
	}

	/** Sends Shutdown downstream.
	 * <p>Executes from worker final block => must not throw.</p>
	 * @param throwable Throwable that caused loop termination
	 */
	private void shutdownDownstream() {
		if (!shutdownForwarded) {
			shutdownForwarded = true;
			downstream.submit(Traits.addTraits(Result.IGNORED, Shutdown.class));
		}
	}

	/** Calls termination callback.
	 * <p>Executes from worker final block => must not throw.</p>
	 */
	private void terminationCallback() {
		if (onTermination != null)
			onTermination.accept(this);
	}

}
