package cz.doleckovi.piskvorky.gtp.command.execution;

import cz.doleckovi.piskvorky.gtp.command.CancelingAction;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.InterruptingAction;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class CommandExecutor extends ThreadPoolExecutor {

	private final Lock lock = new ReentrantLock();
	private Thread currentThread = null;
	private Runnable currentRunnable = null;
	private InterruptingAction lastInterruptingAction = null;
	private CancelingAction lastCancelingAction = null;

	CommandExecutor() {
		super(1, 1, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), Thread.ofPlatform().name("command-processor").factory());
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new ActionTask<>(runnable, value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new ActionTask<>(callable);
	}

	@Override
	protected void beforeExecute(Thread thread, Runnable runnable) {
		boolean interrupt;
		boolean cancel;
		if (runnable instanceof ActionTask<?> actionTask) {
			lock.lock();
			try {
				if (actionTask.action == lastInterruptingAction) {
					lastInterruptingAction = null;
					interrupt = false;
				} else {
					interrupt = lastInterruptingAction != null;
				}
				if (actionTask.action == lastCancelingAction) {
					lastCancelingAction = null;
					cancel = false;
				} else {
					cancel = lastCancelingAction != null;
				}
				currentThread = thread;
				currentRunnable = runnable;
			} finally {
				lock.unlock();
			}
			if (cancel) {
				actionTask.cancel(interrupt);
			}
			if (interrupt) {
				Thread.currentThread().interrupt();
			}
		}
		super.beforeExecute(thread, runnable);
	}

	@Override
	public void execute(Runnable command) {
		lock.lock();
		try {
			boolean interrupt;
			if (command instanceof InterruptingAction interruptingAction) {
				lastInterruptingAction = interruptingAction;
				interrupt = true;
			} else {
				interrupt = false;
			}
			boolean cancel;
			if (command instanceof CancelingAction cancelingAction) {
				lastCancelingAction = cancelingAction;
				cancel = true;
			} else {
				cancel = false;
			}
			if (cancel && currentRunnable instanceof Future<?> future) {
				future.cancel(interrupt);
			} else if (interrupt && currentThread != null) {
				currentThread.interrupt();
			}
		} finally {
			lock.unlock();
		}
		super.execute(command);
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		super.afterExecute(runnable, throwable);
		lock.lock();
		try {
			currentRunnable = null;
			currentThread = null;
			Thread.interrupted();
		} finally {
			lock.unlock();
		}
	}

	public Future<String> submit(Command command) {

	}

}
