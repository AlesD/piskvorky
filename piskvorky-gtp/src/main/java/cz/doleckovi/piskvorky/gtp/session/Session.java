package cz.doleckovi.piskvorky.gtp.session;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.InterruptAction;
import cz.doleckovi.piskvorky.gtp.action.ShutdownAction;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.execution.ActionProcessingLoop;
import cz.doleckovi.piskvorky.gtp.execution.CommandReadingLoop;
import cz.doleckovi.piskvorky.gtp.execution.ResultWritingLoop;
import cz.doleckovi.piskvorky.gtp.execution.SubmitRejectedException;
import cz.doleckovi.piskvorky.gtp.parser.CommandParser;
import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The top level of one GTP protocol conversation. A {@code Session} owns its bound
 * {@link GtpTransport}, the conversation's current {@link Board} and {@link Game}, and
 * the GTP machinery proper - the three pipeline stages ({@link CommandReadingLoop}
 * reading, {@link ActionProcessingLoop} executing, {@link ResultWritingLoop}
 * dispatching), one dedicated worker thread each. It is the single
 * {@link Context} handed to every action the conversation runs.
 *
 * <h2>Lifecycle</h2>
 * Construction mints the initial game state (a {@value #DEFAULT_BOARD_SIZE} board, the
 * spec &sect;4.1 default) and starts all three stages plus a dedicated lifecycle thread:
 * a constructed session is a running session. The conversation ends on the wire
 * ({@code quit}, spec &sect;6.3.1), on EOF or broken input, or host-side through
 * {@link #close()}; whichever cause, the
 * {@link cz.doleckovi.piskvorky.gtp.trait.Shutdown} cascade drains every
 * already-accepted command (executed and answered in order) before the stage workers
 * exit. The lifecycle thread waits for the last worker to leave, then stops and closes
 * the transport and hands the session to the {@code onTermination} callback given at
 * assembly - end-of-life cleanup is what that callback does (for the MVP STDIO session,
 * ending the application).
 *
 * <h2>Termination, not shutdown</h2>
 * There is deliberately no {@code shutdown()}/{@code awaitTermination()} pair.
 * {@link #close()} <em>initiates</em> a host-side end (idempotent; submits the terminal
 * marker so accepted work still drains, then stops the transport input); the
 * {@code onTermination} callback <em>observes</em> the completed end, on the lifecycle
 * thread. A session parked on a live,
 * silent stream (STDIO {@code System.in}, whose {@code stop()} cannot unblock a read
 * already in progress) ends only through {@code quit}/EOF - exactly as before this
 * restructure; {@code close()} is the host-abandonment lever meaningful for transports
 * that <em>can</em> release a parked read (files, sockets with {@code shutdownInput}).
 *
 * <h2>Concurrency of game state</h2>
 * The board and game are read and mutated exclusively by {@link Command}s
 * running on the single {@link ActionProcessingLoop} worker (serial execution,
 * spec &sect;6.2), so the
 * references need no synchronization and {@code boardsize}/{@code clear_board} may
 * replace the game without racing it. The host must not touch game state while the
 * session runs; inside the {@code onTermination} callback it is safe to read - the
 * lifecycle thread's latch wait publishes the last worker's writes.
 *
 * <h2>Ownership</h2>
 * One session per bound conversation. The app wiring (Spring, in {@code piskvorky-app})
 * constructs the session and registers SIGINT against {@link #interrupt()} (the process
 * is the single-session host today; multi-session hosting, TODO.md [10]/[24], creates
 * one session per connection and owns their lifecycle). The session closes the transport
 * it owns; game persistence stays a host-side cleanup concern (TODO.md [24]).
 */
public class Session implements Context, AutoCloseable {

	/** Board size of a fresh session - the GTP default board (spec &sect;4.1). */
	public static final int DEFAULT_BOARD_SIZE = 19;

	/** Pipeline stages whose exit the lifecycle thread waits for. */
	private static final int STAGE_COUNT = 3;

	private static final Logger LOG = Logger.getLogger(Session.class.getName());

	private final GtpTransport transport;
	private final IntFunction<? extends Board> boardFactory;
	private final Function<? super Board, ? extends Game> gameFactory;
	private final Consumer<Session> onTermination;

	// game state - confined to the action-loop worker while running (see class javadoc)
	private Board board;
	private Game game;

	private final ResultWritingLoop results;
	private final ActionProcessingLoop actions;
	/** Counted down once by every stage as it exits; the lifecycle thread waits on it. */
	private final CountDownLatch stageExits = new CountDownLatch(STAGE_COUNT);

	/**
	 * Assembles and starts a session: the transport's responses/messages channels feed
	 * the dispatch and execution stages, its command channel is read by the head, and
	 * the injected factories mint the initial {@value #DEFAULT_BOARD_SIZE} board and its
	 * game. Stages start bottom-up so every hand-off has its sink before the reading
	 * head admits traffic; the lifecycle thread is started last.
	 *
	 * @param name       session name, prefixed to the three stage worker thread names
	 * @param threads    base factory producing the stage worker threads and the lifecycle
	 *                   thread (daemon-ness is the wiring's choice; each thread is renamed
	 *                   {@code name-role})
	 * @param transport  the bound conversation transport, closed by the session at end
	 * @param parser     per-line command parser (this session's own instance - not thread-safe)
	 * @param boardFactory produces a board of a requested size
	 * @param gameFactory  produces the game played on a given board
	 * @param onTermination callback handed the session once the conversation fully ended
	 *                   and the transport was closed, running on the session's lifecycle
	 *                   thread; its own failures are logged, never propagated
	 */
	public Session(String name, ThreadFactory threads, GtpTransport transport, CommandParser parser,
			IntFunction<? extends Board> boardFactory, Function<? super Board, ? extends Game> gameFactory,
			Consumer<Session> onTermination) {
		this.transport = Objects.requireNonNull(transport, "transport");
		this.boardFactory = Objects.requireNonNull(boardFactory, "boardFactory");
		this.gameFactory = Objects.requireNonNull(gameFactory, "gameFactory");
		this.onTermination = Objects.requireNonNull(onTermination, "onTermination");
		this.board = Objects.requireNonNull(boardFactory.apply(DEFAULT_BOARD_SIZE), "initial board");
		this.game = Objects.requireNonNull(gameFactory.apply(board), "initial game");

		this.results = new ResultWritingLoop(stageThreads(threads, name + "-result"),
				transport.responses(), stage -> stageExits.countDown());
		this.actions = new ActionProcessingLoop(stageThreads(threads, name + "-action"), this, results,
				stage -> stageExits.countDown());
		var reading = new CommandReadingLoop(stageThreads(threads, name + "-reading"),
				transport.commands(), Objects.requireNonNull(parser, "parser"), actions,
				stage -> stageExits.countDown());
		var lifecycle = stageThreads(threads, name + "-lifecycle").newThread(this::lifecycle);
		if (lifecycle == null)
			throw new IllegalStateException("ThreadFactory produced no thread: " + threads);
		lifecycle.start();
	}

	/**
	 * Interrupts the running work as the {@code # interrupt} line does: submits the
	 * {@link cz.doleckovi.piskvorky.gtp.trait.Canceling} {@link InterruptAction} to the
	 * action stage, winding down a {@link cz.doleckovi.piskvorky.gtp.trait.Cancelable}
	 * command politely. Never blocks (the interrupt action is not {@code Sync}, so the
	 * action stage's synchronous wait does not apply); dropped silently once the session
	 * is already ending (SIGINT handler target).
	 */
	public void interrupt() {
		try {
			actions.submit(new InterruptAction());
		} catch (SubmitRejectedException alreadyTerminal) {
			LOG.log(Level.FINE, "Interrupt dropped, session already terminal", alreadyTerminal);
		}
	}

	/**
	 * Initiates a host-side end of the conversation: submits the {@link ShutdownAction}
	 * terminal marker to the action stage (so every accepted command still drains, spec
	 * &sect;6.3.1) and stops the transport input. Does not wait - the marker is processed
	 * asynchronously and the completed end, with a settled game state, is observed only
	 * through the {@code onTermination} callback. Idempotent and safe to call from any
	 * thread (including a command body: the marker is accepted-and-last behind the
	 * running task). On a stream whose {@code stop()} cannot release a parked read
	 * (STDIO) the reading worker lingers until {@code quit}/EOF as before.
	 */
	@Override
	public void close() {
		try {
			actions.submit(new ShutdownAction()); // accepted-and-terminal, never blocks
		} catch (SubmitRejectedException alreadyTerminal) {
			LOG.log(Level.FINE, "Close dropped, session already terminal", alreadyTerminal);
		}
		transport.stop();
	}

	// --- Context: the session facet exposed to commands -------------------------------

	@Override
	public LineWriter messages() {
		return transport.messages();
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public void setBoard(int size) {
		var newBoard = Objects.requireNonNull(boardFactory.apply(size), "board");
		var newGame = Objects.requireNonNull(gameFactory.apply(newBoard), "game");
		this.board = newBoard;
		this.game = newGame;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public void resetGame() {
		this.game = Objects.requireNonNull(gameFactory.apply(board), "game");
	}

	/** Renames each worker the base factory produces, keeping the wiring's daemon choice. */
	private static ThreadFactory stageThreads(ThreadFactory base, String threadName) {
		return runnable -> {
			var thread = base.newThread(runnable);
			if (thread != null)
				thread.setName(threadName);
			return thread;
		};
	}

	/** The lifecycle thread body: waits for every stage worker to exit, then runs the
	 * single teardown. Interrupts are not expected; the wait resumes regardless, so the
	 * teardown is always reached. */
	private void lifecycle() {
		var interrupted = false;
		for (;;)
			try {
				stageExits.await();
				break;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		if (interrupted)
			Thread.currentThread().interrupt();
		finish();
	}

	/** The single teardown, run on the lifecycle thread once all stage workers exited:
	 * release the transport, then hand the ended session to the host callback. */
	private void finish() {
		try {
			transport.stop();
			transport.close(); // flushes both output channels, releases owned resources
			LOG.info("GTP session ended, transport closed");
		} catch (RuntimeException closeFailure) {
			LOG.log(Level.WARNING, "Transport close failed", closeFailure);
		} finally {
			notifyTermination();
		}
	}

	/** Hands the ended session to the host callback; a failing callback is logged and
	 * swallowed - the session's end is already a fact. */
	private void notifyTermination() {
		try {
			onTermination.accept(this);
		} catch (RuntimeException e) {
			LOG.log(Level.WARNING, "Termination callback failed", e);
		}
	}

}
