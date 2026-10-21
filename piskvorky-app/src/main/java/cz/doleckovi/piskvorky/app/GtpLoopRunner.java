package cz.doleckovi.piskvorky.app;

import cz.doleckovi.piskvorky.gtp.execution.IntSignalHandler;
import cz.doleckovi.piskvorky.gtp.session.Session;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.DefaultLifecycleProcessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/** Runs GTP session.
 * <p>The session is tied to application context lifecycle.</p>
 */
@Component
@Profile("gtp")
class GtpLoopRunner implements SmartLifecycle {

	private final Session session;
	private final AtomicBoolean running = new AtomicBoolean();

	GtpLoopRunner(Session session) {
		this.session = session;
	}

	@Override
	public void start() {
		if (!running.compareAndSet(false, true))
			return;
		IntSignalHandler.registerInterruptHandler(new IntSignalHandler(session::interrupt));
	}

	@Override
	public void stop() {
		if (!running.compareAndSet(true, false))
			return;
		IntSignalHandler.unregisterInterruptHandler();
		// session.close() is not called here - it runs via the bean's destroy method,
		// which DefaultLifecycleProcessor reaches after lifecycle beans are stopped.
	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

}
