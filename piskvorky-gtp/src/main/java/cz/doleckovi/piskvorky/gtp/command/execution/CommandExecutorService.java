package cz.doleckovi.piskvorky.gtp.command.execution;

import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.trait.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** Extension of {@link java.util.concurrent.ExecutorService} that handles command traits.
 * <p>Futures returned from this service will be proxies with all traits of submitted tasks.</p>
 */
public class CommandExecutorService extends ThreadPoolExecutor {

    private final static Logger LOG = LoggerFactory.getLogger(CommandExecutorService.class);

	private final Lock lock = new ReentrantLock();
	private Thread currentThread = null;
	private Runnable currentRunnable = null;
	private Interrupting lastInterrupting = null;
	private Canceling lastCanceling = null;

	public CommandExecutorService() {
		super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
				Thread.ofPlatform().name("command-processor").factory());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return (RunnableFuture<T>) Traits.copyTraits(runnable, new FutureTask<>(runnable, value));
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		if (callable instanceof Command command)
			return (RunnableFuture<T>) Traits.copyTraits(command, new CommandFutureTask(command));
		return (RunnableFuture<T>) Traits.copyTraits(callable, new FutureTask<>(callable));
	}

    @Override
    public void execute(Runnable runnable) {
        lock.lock();
        Canceling canceling = null;
        Cancelable cancelable = null;
        Future<?> future = null;
        try {
            if (runnable instanceof Interrupting interruptingRunnable) {
                lastInterrupting = Traits.getOrigin(interruptingRunnable, Interrupting.class);
                LOG.trace("Interrupting operation accepted for execution: {}", lastInterrupting);
                if (currentRunnable instanceof Interruptible interruptibleRunnable && !currentThread.isInterrupted()) {
                    LOG.info("Interrupting {}", Traits.getOrigin(interruptibleRunnable, Interruptible.class));
                    currentThread.interrupt();
                }
            }
            if (runnable instanceof Canceling cancelingRunnable) {
                lastCanceling = canceling = Traits.getOrigin(cancelingRunnable, Canceling.class);
                LOG.trace("Cancelling operation accepted for execution: {}", lastCanceling);
                if (currentRunnable instanceof Cancelable cancelableRunnable) {
                    cancelable = Traits.getOrigin(cancelableRunnable, Cancelable.class);
                    if (currentRunnable instanceof Future<?> runnableFuture)
                        future = Traits.getOrigin(runnableFuture, Future.class);
                }
            }
        } finally {
            lock.unlock();
        }
        cancel(canceling, cancelable, future);
        super.execute(runnable);
    }

    private void cancel(Canceling canceling, Cancelable cancelable, Future future) {
        if (cancelable != null) {
            LOG.info("Canceling {}", cancelable);
            if (future == null) {
                Object result = null;
                Exception exception = null;
                try {
                    result = cancelable.cancel(canceling);
                    LOG.debug("Canceling call returned {}:", result);
                } catch (Exception e) {
                    LOG.debug("Canceling call threw {}:", e.getClass(), e.getMessage());
                    exception = e;
                }
                LOG.debug("Cancellation callback to: {}", canceling);
                canceling.canceled(cancelable, result, exception);
            } else {
                cancelable.cancel(canceling, future);
                LOG.debug("Cancellation callback with {} future to: {}", future.isCancelled() ? "cancelled" :
                        future.isDone() ? "done" : "running", canceling);
                canceling.canceled(cancelable, future);
            }
        }
    }

    @Override
	protected void beforeExecute(Thread thread, Runnable runnable) {
        lock.lock();
        try {
            if (lastCanceling != null && (runnable instanceof Cancelable cancelable) && (runnable instanceof Future future)) {
                LOG.info("Canceling before execution: {}", Traits.getOrigin(cancelable, Cancelable.class));
                future.cancel(false);
            } else {
                currentThread = thread;
                currentRunnable = runnable;
                if (lastInterrupting != null && (runnable instanceof Interruptible interruptible) && !currentThread.isInterrupted()) {
                    LOG.info("Interrupting before execution: {}", Traits.getOrigin(interruptible, Interruptible.class));
                    thread.interrupt();
                }
            }
        } finally {
            lock.unlock();
        }
		super.beforeExecute(thread, runnable);
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		super.afterExecute(runnable, throwable);
		lock.lock();
		try {
			currentRunnable = null;
			currentThread = null;
            if ((runnable instanceof Canceling canceling)
                    && Traits.getOrigin(canceling, Canceling.class) == lastCanceling) {
                lastCanceling = null;
                LOG.trace("Cleared last cancelling");
            }
            if ((runnable instanceof Interrupting interrupting)
                    && Traits.getOrigin(interrupting, Interrupting.class) == lastInterrupting) {
                lastInterrupting = null;
                LOG.trace("Cleared last interrupting");
            }
		} finally {
			lock.unlock();
		}
	}

}
