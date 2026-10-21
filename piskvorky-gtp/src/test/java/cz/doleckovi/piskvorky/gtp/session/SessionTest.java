package cz.doleckovi.piskvorky.gtp.session;

import cz.doleckovi.piskvorky.core.GameImpl;
import cz.doleckovi.piskvorky.core.board.BoardImpl;
import cz.doleckovi.piskvorky.gtp.action.ActionFactory;
import cz.doleckovi.piskvorky.gtp.command.CompositeCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.GtpCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.gogui.GoGuiCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesCommandFactory;
import cz.doleckovi.piskvorky.gtp.parser.AntlrCommandParser;
import cz.doleckovi.piskvorky.gtp.transport.CommandReader;
import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;
import cz.doleckovi.piskvorky.gtp.transport.StreamTransport;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * End-to-end test of the top-level {@link Session} (TODO.md [29]): the owned pipeline
 * (reading &rarr; action &rarr; result) answers the administrative command set over an
 * in-memory transport, {@code quit} and plain EOF both end the conversation per spec
 * &sect;6.3.1, the session closes its transport at end of life, game-state commands
 * round-trip through the session-as-Context, and the end-of-life callback handed at
 * assembly fires exactly once, after the transport was closed.
 */
class SessionTest {

	private final StringWriter responses = new StringWriter();
	private final StringWriter messages = new StringWriter();
	private final CountDownLatch ended = new CountDownLatch(1);
	private final AtomicInteger callbackRuns = new AtomicInteger();

	private GtpTransport transport(String input) {
		return StreamTransport.builder(new StringReader(input), responses, messages).build();
	}

	private static ThreadFactory daemonFactory() {
		return runnable -> {
			var thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		};
	}

	private static CompositeCommandFactory allCommandsFactory() {
		return new CompositeCommandFactory(new GtpCommandFactory(), new GoGuiCommandFactory(), new RulesCommandFactory());
	}

	private Session session(GtpTransport transport) {
		var parser = new AntlrCommandParser(allCommandsFactory(), new ActionFactory() { });
		return new Session("test", daemonFactory(), transport, parser, BoardImpl::new, GameImpl::new,
				session -> {
					callbackRuns.incrementAndGet();
					ended.countDown();
				});
	}

	private void awaitEnd() throws Exception {
		assertThat(ended.await(5, TimeUnit.SECONDS)).as("session end callback fired").isTrue();
	}

	private String runToEnd(String input) throws Exception {
		session(transport(input));
		awaitEnd();
		return responses.toString();
	}

	// ---- conversation over the wire ------------------------------------------------------

	@Test
	void administrativeCommandsAnsweredInOrderOverTheWire() throws Exception {
		var wire = runToEnd("""
				protocol_version
				name
				known_command name
				known_command dancing_elephant
				list_commands
				quit
				""");
		var blocks = wire.split("\n\n");
		assertThat(blocks).hasSize(6);
		assertThat(blocks[0]).isEqualTo("= 2");
		assertThat(blocks[1]).isEqualTo("= Piskvorky");
		assertThat(blocks[2]).isEqualTo("= true");
		assertThat(blocks[3]).isEqualTo("= false");
		assertThat(blocks[4]).startsWith("= ").contains("quit", "name", "list_commands");
		assertThat(blocks[5]).isEqualTo("=");
	}

	@Test
	void quitIsLastCommandRead() throws Exception {
		// lines queued behind quit must never be executed (spec §6.3.1)
		assertThat(runToEnd("name\nquit\nname\n")).isEqualTo("= Piskvorky\n\n=\n\n");
	}

	@Test
	void eofWithoutQuitTerminalizesCleanly() throws Exception {
		assertThat(runToEnd("""
				name
				protocol_version
				""".stripIndent()
		))
				.as("outstanding answers still flushed at EOF")
				.isEqualTo("= Piskvorky\n\n= 2\n\n");
	}

	@Test
	void blankAndCommentLinesProduceNoOutput() throws Exception {
		assertThat(runToEnd("""

				# just a comment


				name
				quit
				"""))
				.as("no-op lines dispatch as IGNORED without wire bytes")
				.isEqualTo("= Piskvorky\n\n=\n\n");
	}

	@Test
	void malformedLineAnswersWithSyntaxError() throws Exception {
		var first = runToEnd("42 bogus !\nquit\n").split("\n\n", 2)[0];
		assertThat(first).as("recognized id echoed in the ? answer (spec §3.3)").startsWith("? 42");
	}

	// ---- session owns its transport ----------------------------------------------------------

	@Test
	void transportStoppedAndClosedWhenSessionEnds() throws Exception {
		var transport = transport("quit\n");
		// the shared fields are written by this test only before construction; reading
		// them after awaitEnd() is safe (happens-before via the callback's latch)
		session(transport);
		awaitEnd();
		assertThat(transport.stopped()).isTrue();
		assertThatThrownBy(() -> transport.responses().writeln("late"))
				.as("teardown closed the transport outputs")
				.isInstanceOf(RejectedExecutionException.class);
	}

	// ---- game lifecycle through the session Context ------------------------------------------

	@Test
	void boardsizeReplacesTheGameVisibleToCommands() throws Exception {
		assertThat(runToEnd("boardsize 5\ngogui-rules_board_size\nquit\n"))
				.as("setBoard replaced board+game; a later command reads the new size")
				.contains("= 5");
	}

	// ---- end-of-life callback -------------------------------------------------------------

	@Test
	void terminationCallbackFiresExactlyOnce() throws Exception {
		session(transport("quit\n"));
		awaitEnd();
		Thread.sleep(100); // let any spurious extra invocation show up
		assertThat(callbackRuns).as("one callback invocation per session").hasValue(1);
	}

	@Test
	void throwingTerminationCallbackDoesNotUndoTeardown() throws Exception {
		var ran = new CountDownLatch(1);
		var transport = transport("quit\n");
		var parser = new AntlrCommandParser(allCommandsFactory(), new ActionFactory() { });
		new Session("test", daemonFactory(), transport, parser, BoardImpl::new, GameImpl::new, session -> {
			ran.countDown();
			throw new IllegalStateException("broken cleanup");
		});
		assertThat(ran.await(5, TimeUnit.SECONDS)).as("the callback ran despite its own failure").isTrue();
		assertThat(transport.stopped())
				.as("teardown ran before the callback and was not undone by its failure")
				.isTrue();
	}

	@Test
	void interruptAfterEndIsTolerated() throws Exception {
		var session = session(transport("quit\n"));
		awaitEnd();
		assertThatCode(session::interrupt).doesNotThrowAnyException();
	}

	@Test
	void closeReleasesParkedReaderAndEndsSession() throws Exception {
		var parked = new ParkingTransport();
		var session = session(parked);
		session.close();
		awaitEnd();
		assertThat(parked.stopped()).isTrue();
	}

	@Test
	void closeIsIdempotentAfterNaturalEnd() throws Exception {
		var session = session(transport("quit\n"));
		awaitEnd();
		assertThatCode(() -> {
			session.close();
			session.close();
		}).doesNotThrowAnyException();
	}

	/**
	 * Transport whose command input never delivers a line: the reading worker parks in
	 * {@link CommandReader#readLine()} until {@link GtpTransport#stop()} releases it -
	 * the host-abandonment shape a socket transport will have.
	 */
	private static final class ParkingTransport implements GtpTransport {

		private volatile boolean stopped;

		@Override
		public CommandReader commands() {
			return () -> {
				while (!stopped)
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return null;
					}
				return null;
			};
		}

		@Override
		public LineWriter responses() {
			return block -> { };
		}

		@Override
		public LineWriter messages() {
			return block -> { };
		}

		@Override
		public boolean stopped() {
			return stopped;
		}

		@Override
		public void stop() {
			stopped = true;
		}

		@Override
		public void close() {
			stop();
		}

	}

}
