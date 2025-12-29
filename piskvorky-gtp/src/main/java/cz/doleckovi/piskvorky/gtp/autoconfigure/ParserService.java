package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.parser.GTPParser;
import org.springframework.context.Lifecycle;

import java.util.concurrent.atomic.AtomicReference;

public class ParserService implements Lifecycle {

	private final AtomicReference<Thread> executingThread = new AtomicReference<>();

	private GTPParser parser;

	private void execute() {
		var currentThread = Thread.currentThread();
		while (!currentThread.isInterrupted() || executingThread.get() == currentThread) {
			var action = parser.action();
		}
		executingThread.compareAndSet(currentThread, null);
	}

	@Override
	public void start() {
		var thread = executingThread.get();
		if (thread == null) {
			thread = Thread.ofPlatform().unstarted(this::execute);
			if (executingThread.compareAndSet(null, thread))
				thread.start();
		}
	}

	@Override
	public void stop() {
		var thread = executingThread.getAndSet(Thread.currentThread());
		try {
			if (thread != null) {
				while (thread.isAlive()) {
					thread.interrupt();
					thread.join(1000);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			executingThread.compareAndSet(Thread.currentThread(), null);
		}
	}

	@Override
	public boolean isRunning() {
		var thread = executingThread.get();
		return thread != null && thread.isAlive();
	}

}
