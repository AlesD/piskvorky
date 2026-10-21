package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.InterruptAction;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** GoGui's INT signal handler — mechanism 2 of the two interrupt channels described in
 * the GoGui manual, ch. 10 <em>Interrupting Commands</em> ({@code docs/gogui/interrupting-commands.md}).
 * <p>GoGui sends SIGINT to the engine only if the engine implements the
 * {@code gogui-sigint} command and returns its process ID ({@code kill -INT pid} is then
 * invoked); handling the signal is advertised by that command name (TODO.md [20] covers
 * answering it). On arrival this handler runs the injected {@code interruptor}, which
 * performs the session-side {@link InterruptAction} submission.</p>
 * <p>The application wiring owning the active session calls
 * {@link #registerInterruptHandler} and {@link #unregisterInterruptHandler} on
 * takeover, one registration per process (TODO.md [22]).</p>
 * <p>Mechanism 1, the {@code # interrupt} comment line on the GTP input stream, is an
 * independent channel that never passes through this class. Ch. 10 restricts the signal
 * mechanism to UNIX and it cannot carry over network GTP streams.</p>
 */
public class IntSignalHandler implements SignalHandler {

	private static final Logger LOG = Logger.getLogger(IntSignalHandler.class.getName());
	private static final Signal SIGINT = new Signal("INT");
	private static SignalHandler previousHandler = null;

	public static synchronized void registerInterruptHandler(SignalHandler signalHandler) {
		previousHandler = Signal.handle(SIGINT, signalHandler);
		LOG.info("Interrupt signal handler registered");
	}

	public static synchronized void unregisterInterruptHandler() {
		if (previousHandler != null)
			Signal.handle(SIGINT, previousHandler);
		previousHandler = null;
		LOG.info("Interrupt signal handler unregistered");
	}

	private final Runnable interruptor;

	/** @param interruptor runs the interrupt against the active session; a
	 *                    {@link RejectedExecutionException} it throws - the session
	 *                    already ending - is tolerated here */
	public IntSignalHandler(Runnable interruptor) {
		this.interruptor = interruptor;
	}

	@Override
	public void handle(Signal sig) {
		LOG.severe("Caught INT signal");
		try {
			interruptor.run();
		} catch (SubmitRejectedException e) {
			LOG.log(Level.FINE, "Interrupt dropped, session already terminal", e);
		}
	}

}
