package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.trait.Traits;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Formats {@link Result}s and writes them to {@link LineWriter}. */
public class ResultWritingLoop implements ExecutionLoop<Result, Result> {

	private record QueueItem(Result result, CompletableFuture<Result> future) {}

	private static final Logger LOG = Logger.getLogger(ResultWritingLoop.class.getName());

	private final LineWriter responses;
	private final Consumer<? super ExecutionLoop<Result, Result>> onTermination;
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition resultAvailable = lock.newCondition();
	// Internal state guarded by the lock
	private final LinkedBlockingQueue<QueueItem> queue = new LinkedBlockingQueue<>();
	private boolean accepting = true;

	/** Creates the stage and starts its worker thread.
	 * @param threadFactory factory producing the worker thread
	 * @param responses     channel the framed results are written to
	 * @param onTermination hook invoked exactly once, on the exiting worker thread
	 */
	public ResultWritingLoop(ThreadFactory threadFactory, LineWriter responses,
			Consumer<? super ExecutionLoop<Result, Result>> onTermination) {
		this.responses = Objects.requireNonNull(responses, "responses");
		this.onTermination = onTermination;
		var worker = Objects.requireNonNull(threadFactory, "threadFactory").newThread(this::loop);
		if (worker == null)
			throw new IllegalStateException("ThreadFactory produced no thread: " + threadFactory);
		worker.start();
	}

	@Override
	public Future<Result> submit(Result task) throws SubmitRejectedException {
		Objects.requireNonNull(task, "task");
		LOG.log(Level.FINEST, () -> "Result submitted: " + task);
		var future = new CompletableFuture<Result>();
		lock.lock();
		try {
			if (!accepting)
				throw new SubmitRejectedException("New tasks are not accepted");
			if (task instanceof Shutdown) {
				accepting = false;
				LOG.log(Level.INFO, "Shutdown - new tasks will not be accepted");
			}
			queue.add(new QueueItem(task, future));
			resultAvailable.signal();
		} finally {
			lock.unlock();
		}
		return future;
	}

	private void loop() {
		Throwable throwable = null;
		try {
			QueueItem queueItem;
			do {
				queueItem = removeTask();
				process(queueItem.result, queueItem.future);
			} while (!(queueItem.result instanceof Shutdown));
		} catch (InterruptedException e) {
			LOG.log(Level.WARNING, "Interrupt in result writing loop");
		} catch (Error|RuntimeException e) {
			LOG.log(Level.SEVERE, "Unhandled exception in result writing loop", e);
			throwable = e;
		} finally {
			throwable = executeSafely(throwable, "Failed to drain queue", this::drain);
			throwable = executeSafely(throwable, "Termination callback failure", this::terminationCallback);
			if (throwable instanceof Error error)
				throw error;
		}
	}

	private QueueItem removeTask() throws InterruptedException {
		lock.lock();
		try {
			while (queue.isEmpty())
				resultAvailable.await(1000, TimeUnit.MILLISECONDS);
			return queue.remove();
		} finally {
			lock.unlock();
		}
	}

	private void process(Result result, CompletableFuture<Result> future) {
		try {
			var formatted = result.format();
			if (formatted != null) {
				responses.writeln(formatted);
			}
			future.complete(result);
			if (result instanceof Shutdown)
				LOG.info("Shutdown processed");
		} catch (IOException|RuntimeException e) {
			future.completeExceptionally(e);
			LOG.log(Level.WARNING, e, () -> "Failed to write response for result: " + result);
			sendShutdown();
		} catch (Error e) {
			future.completeExceptionally(e);
			throw e;
		}
	}

	private void sendShutdown() {
		lock.lock();
		try {
			if (accepting) {
				accepting = false;
				var shutdownMark = Traits.addTraits(Result.IGNORED, Shutdown.class);
				queue.add(new QueueItem(shutdownMark, new CompletableFuture<>()));
				LOG.log(Level.INFO, "Sent self shutdown");
			}
		} finally {
			lock.unlock();
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

	private void terminationCallback() {
		if (onTermination != null)
			onTermination.accept(this);
	}

}
