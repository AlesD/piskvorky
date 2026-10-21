package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.parser.CommandParser;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.trait.Traits;
import cz.doleckovi.piskvorky.gtp.transport.CommandReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * Covers the {@link CommandReadingLoop} head stage: the pump parses wire lines and
 * dispatches the resulting actions in order, the terminalization protocol (EOF, broken
 * input, a host-submitted {@code quit} action), terminalization of the dispatched
 * {@link Shutdown}-carrying {@code quit} and the bare future handed back from {@code submit},
 * the drop of
 * an action that raced a terminal submission, the head's use as an {@link ExecutionLoop}
 * of {@link Action}s receiving injected work without wire activity, and the termination
 * hook fired on worker exit (TODO.md [7] pipeline mechanics).
 */
class CommandReadingLoopTest {

	// ---- task / reader / downstream fixtures -------------------------------------------

	/** Parser stand-in: the line <em>is</em> the action name, plus the {@code quit} special. */
	private static final CommandParser STUB_PARSER = line -> switch (line) {
		case "quit" -> new QuitAction();
		default -> new StubAction(line);
	};

	private record StubAction(String name) implements Action {
		@Override
		public Result execute(Context context) {
			return Result.IGNORED;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/** Wire {@code quit} as the reading loop sees it: a {@code Shutdown} carrier. */
	private record QuitAction() implements Action, Shutdown {
		@Override
		public Result execute(Context context) {
			return Result.IGNORED;
		}

		@Override
		public String toString() {
			return "quit";
		}
	}

	/**
	 * Queue-fed {@link CommandReader}: reads block until lines are fed; {@code end()} /
	 * {@code failWith()} release parked reads; queued lines always win over both.
	 */
	private static final class ScriptedReader implements CommandReader {
		private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
		private volatile boolean eof;
		private volatile IOException failure;

		void feed(String... lines) {
			queue.addAll(List.of(lines));
		}

		void end() {
			eof = true;
		}

		void failWith(IOException e) {
			failure = e;
		}

		@Override
		public String readLine() throws IOException {
			while (true) {
				try {
					var line = queue.poll(50, TimeUnit.MILLISECONDS);
					if (line != null)
						return line;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new IOException("interrupted test reader", e);
				}
				if (failure != null)
					throw failure;
				if (eof)
					return null;
			}
		}
	}

	/**
	 * Downstream mirroring {@link ActionProcessingLoop} submission semantics: accepts
	 * until the first {@link Shutdown} carrier (then rejects everything), records the
	 * accepted actions and returns a completed future claiming no traits itself.
	 */
	private static final class RecordingDownstream implements ExecutionLoop<Action, Result> {
		final List<Action> received = new CopyOnWriteArrayList<>();
		private volatile boolean terminal;

		@Override
		public Future<Result> submit(Action task) {
			if (terminal)
				throw new SubmitRejectedException("terminalized already");
			if (task instanceof Shutdown)
				terminal = true;
			received.add(task);
			var outcome = Traits.copyTraits(task, Result.IGNORED);
			return CompletableFuture.completedFuture(outcome);
		}

		/** Kills acceptance without recording anything (external sink death). */
		void kill() {
			terminal = true;
		}

		List<String> names() {
			return received.stream().map(Object::toString).toList();
		}
	}

	// ---- wiring helpers ----------------------------------------------------------------

	private static ThreadFactory daemonFactory() {
		return runnable -> {
			var thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		};
	}

	/** A head stage plus the wiring-side view of its end: the termination latch and hook payload. */
	private static final class Head {
		final CountDownLatch terminated = new CountDownLatch(1);
		final AtomicReference<ExecutionLoop<Action, Result>> hook = new AtomicReference<>();
		final CommandReadingLoop loop;

		Head(CommandReader reader, CommandParser parser, ExecutionLoop<Action, Result> downstream) {
			loop = new CommandReadingLoop(daemonFactory(), reader, parser, downstream, stage -> {
				hook.set(stage);
				terminated.countDown();
			});
		}

		void awaitTermination() {
			try {
				assertThat(terminated.await(2, TimeUnit.SECONDS)).as("worker exited").isTrue();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("interrupted while awaiting");
			}
		}
	}

	// ---- pump: parse and dispatch in order -----------------------------------------------

	@Test
	void linesAreParsedAndDispatchedInOrder() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		reader.feed("a", "b", "c");
		reader.end();
		head.awaitTermination();
		assertThat(downstream.names()).containsExactly("a", "b", "c", "ShutdownAction[]");
	}

	@Test
	void submissionFuturesAreBare() {
		// sanity-check the fake's futures claim no traits themselves
		var downstream = new RecordingDownstream();
		assertThat(downstream.submit(new QuitAction()))
				.isNotInstanceOf(Shutdown.class)
				.isNotInstanceOf(Canceling.class);
	}

	// ---- the head as an ExecutionLoop ----------------------------------------------------

	@Test
	void injectedActionsReachTheDownstreamWithoutWireActivity() throws Exception {
		var reader = new ScriptedReader(); // never fed: the worker stays parked in readLine
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		var outcome = head.loop.submit(new StubAction("injected"));
		assertThat(downstream.names()).containsExactly("injected");
		assertThat(outcome).as("the downstream future is handed back to the submitter").isDone();
		assertThat(outcome.get(1, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
		reader.end();
		head.awaitTermination();
		assertThat(downstream.names()).containsExactly("injected", "ShutdownAction[]");
	}

	@Test
	void submittedQuitFutureStaysBare() throws Exception {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		assertThat(head.loop.submit(new QuitAction()))
				.as("terminality is carried by the action, not the handed-back future")
				.isNotInstanceOf(Shutdown.class);
		assertThat(downstream.names()).containsExactly("quit");
		reader.end();
		head.awaitTermination();
	}

	@Test
	void nullActionIsRejected() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		assertThatThrownBy(() -> head.loop.submit(null)).isInstanceOf(NullPointerException.class);
	}

	// ---- terminalization -----------------------------------------------------------------

	@Test
	void eofDispatchesExactlyOneTerminalMarker() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		reader.feed("move");
		reader.end();
		head.awaitTermination();
		assertThat(downstream.received.get(1))
				.as("EOF marker carries the terminal trait set (Shutdown implies Canceling)")
				.isInstanceOf(Shutdown.class)
				.isInstanceOf(Canceling.class);
		assertThat(downstream.names()).endsWith("ShutdownAction[]");
		// an action submitted after the pipeline terminalized is refused by the head
		assertThatThrownBy(() -> head.loop.submit(new StubAction("late")))
				.isInstanceOf(SubmitRejectedException.class);
		assertThat(downstream.received).hasSize(2);
	}

	@Test
	void quitEndsSessionWithoutAnEofMarker() throws Exception {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		reader.feed("before", "quit");
		head.awaitTermination();
		assertThat(downstream.names()).containsExactly("before", "quit");
		// input still open behind quit: the worker is gone, nothing further is read (spec §6.3.1)
		reader.feed("after");
		reader.end();
		Thread.sleep(150);
		assertThat(downstream.names()).containsExactly("before", "quit");
	}

	@Test
	void terminalActionWhileParkedEndsDownstreamAndDropsLateLine() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		head.loop.submit(new QuitAction()); // host-side end while the worker is parked in readLine
		assertThat(downstream.names()).containsExactly("quit");
		reader.feed("late"); // wakes the worker: parsed, then refused as post-terminal
		reader.end();
		head.awaitTermination();
		assertThat(downstream.names()).as("the late line was discarded").containsExactly("quit");
	}

	@Test
	void brokenInputStillTerminalizesDownstream() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		reader.feed("first");
		reader.failWith(new IOException("pipe down"));
		head.awaitTermination();
		// the dispatch stage survives write failures and IGNORED writes no bytes,
		// so the marker is safe - and required - here
		assertThat(downstream.names()).containsExactly("first", "ShutdownAction[]");
	}

	@Test
	void deadDownstreamEndsWorkerQuietly() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		downstream.kill(); // sink gone before the session even starts
		var head = new Head(reader, STUB_PARSER, downstream);
		reader.feed("doomed");
		reader.end();
		head.awaitTermination();
		assertThat(downstream.names())
				.as("refused dispatch ends the worker; no marker is even attempted")
				.isEmpty();
	}

	@Test
	void secondTerminalSubmissionIsRefused() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		head.loop.submit(new QuitAction());
		assertThatThrownBy(() -> head.loop.submit(new QuitAction()))
				.as("terminality is observable as the documented rejection")
				.isInstanceOf(SubmitRejectedException.class);
		assertThat(downstream.names()).containsExactly("quit");
	}

	@Test
	void injectedActionsAreRefusedAtTheHeadAfterTerminalSubmission() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		head.loop.submit(new QuitAction());
		// the head closes its own acceptance: a work-injecting host is refused here,
		// it never reaches the - for the moment still living - downstream
		assertThatThrownBy(() -> head.loop.submit(new StubAction("late")))
				.isInstanceOf(SubmitRejectedException.class);
		assertThat(downstream.names()).as("the refusal happened at the head").containsExactly("quit");
		reader.end();
		head.awaitTermination();
		assertThat(downstream.names()).as("no EOF marker follows the quit").containsExactly("quit");
	}

	@Test
	void terminationHookFiresWithWorkerExit() throws Exception {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		var head = new Head(reader, STUB_PARSER, downstream);
		assertThat(head.terminated.getCount()).as("running head is not terminated").isEqualTo(1);
		reader.end();
		head.awaitTermination();
		assertThat(head.hook.get()).as("the hook receives the ended head itself").isSameAs(head.loop);
	}

	@Test
	void nullCollaboratorsAreRejectedAtAssembly() {
		var reader = new ScriptedReader();
		var downstream = new RecordingDownstream();
		assertThatThrownBy(() -> new CommandReadingLoop(daemonFactory(), null, STUB_PARSER, downstream,
				stage -> { }))
				.isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> new CommandReadingLoop(daemonFactory(), reader, null, downstream, stage -> { }))
				.isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> new CommandReadingLoop(daemonFactory(), reader, STUB_PARSER, null, stage -> { }))
				.isInstanceOf(NullPointerException.class);
	}

}
