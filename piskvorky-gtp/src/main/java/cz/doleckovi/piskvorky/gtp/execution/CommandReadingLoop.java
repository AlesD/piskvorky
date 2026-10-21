package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.action.ShutdownAction;
import cz.doleckovi.piskvorky.gtp.parser.CommandParser;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.transport.CommandReader;
import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Reads command lines, parses them into actions and submits them downstream.
 * <p>Head of processing pipeline. Reads command lines, but also allows
 * injection of actions into pipeline. Reading and parsing happens on separate
 * thread, action injection is just handover. Both enter execution through
 * {@code submit}.</p>
 * <p>The head accepts until it forwards a {@link Shutdown}-carrying action; every
 * later {@code submit} is refused here, without touching the downstream, and the pump
 * ends. On EOF and broken input the head submits its own terminal marker.</p>
 * <p>The loop owns no resources. Ending the input ({@link GtpTransport#stop()}/
 * {@link GtpTransport#close()}) stays with the wiring that built the transport.</p>
 */
public class CommandReadingLoop implements ExecutionLoop<Action, Result> {

	private static final Logger LOG = Logger.getLogger(CommandReadingLoop.class.getName());

	private final AtomicBoolean shutdownSubmitted = new AtomicBoolean(false);
	private final CommandReader reader;
	private final CommandParser parser;
	private final ExecutionLoop<Action, Result> downstream;
	private final Consumer<? super ExecutionLoop<Action, Result>> onTermination;
	private final Thread worker;

	/** Constructs loop instance and starts worker thread.
	 * @param threadFactory factory producing the worker
	 * @param reader command lines input
	 * @param parser single-line command parser
	 * @param downstream execution stage receiving every submitted action
	 * @param onTermination hook invoked exactly once, on worker thread exit
	 */
	public CommandReadingLoop(ThreadFactory threadFactory,
			CommandReader reader, CommandParser parser,
			ExecutionLoop<Action, Result> downstream,
			Consumer<? super ExecutionLoop<Action, Result>> onTermination)
	{
		this.reader = Objects.requireNonNull(reader, "reader");
		this.parser = Objects.requireNonNull(parser, "parser");
		this.downstream = Objects.requireNonNull(downstream, "downstream");
		this.onTermination = onTermination;
		this.worker = threadFactory.newThread(this::readLoop);
		if (worker == null)
			throw new IllegalStateException("ThreadFactory produced no thread: " + threadFactory);
		worker.start();
	}

	/**
	 * {@inheritDoc}
	 * <p>The action is forwarded to the downstream stage on the submitting
	 * thread; the downstream's future is handed back to the caller. A
	 * {@link Shutdown}-carrying action (a {@code quit}) ends loop execution
	 * and every later submission is rejected.</p>
	 */
	@Override
	public Future<Result> submit(Action action) throws SubmitRejectedException {
		Objects.requireNonNull(action, "action");
		if (shutdownSubmitted.get())
			throw new SubmitRejectedException("Shutdown already submitted, new tasks are not accepted");
		if (action instanceof Shutdown) {
			shutdownSubmitted.set(true);
			LOG.log(Level.INFO, "Shutdown - new tasks will not be accepted");
		}
		return downstream.submit(action);
	}

	private void readLoop() {
		Throwable throwable = null;
		try {
			while (!shutdownSubmitted.get()) {
				var line = reader.readLine();
				if (line == null) {
					LOG.log(Level.INFO, "No more commands to read");
					return;
				}
				submit(parser.parse(line));
			}
		} catch (SubmitRejectedException e) {
			LOG.log(Level.WARNING, "Failed to submit downstream", e);
			shutdownSubmitted.set(true); // Don't try to send Shutdown
			throwable = e;
		} catch (IOException|RuntimeException e) {
			LOG.log(Level.INFO, "Failed to process command line", e);
			throwable = e;
		} catch (Error e) {
			throwable = e;
		} finally {
			throwable = executeSafely(throwable, "Failed to submit shutdown action", this::sendShutdown);
			throwable = executeSafely(throwable, "Termination callback failure", this::terminationCallback);
			if (throwable instanceof Error error)
				throw error;
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

	private void sendShutdown() {
		if (shutdownSubmitted.compareAndSet(false, true))
			downstream.submit(new ShutdownAction());
	}

	private void terminationCallback() {
		if (onTermination != null)
			onTermination.accept(this);
	}

}
