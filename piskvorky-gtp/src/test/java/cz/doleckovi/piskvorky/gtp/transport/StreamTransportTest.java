package cz.doleckovi.piskvorky.gtp.transport;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

class StreamTransportTest implements WithAssertions {

	@Test
	void allThreeDestinationsAreRequired() {
		var in = new TrackingStringReader("x");
		var out = new RecordingWriter();
		assertThatThrownBy(() -> new StreamTransportBuilder(in, out, null))
				.as("no default destination for diagnostics")
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	void channelsBuiltFromOneDestinationShareIt() {
		var sink = new RecordingWriter();
		var transport = StreamTransport.builder(new TrackingStringReader("x"), sink, sink).build();
		assertThat(transport.messages()).as("same base writer -> one LockingLineWriter")
				.isSameAs(transport.responses());
		transport.close();
		// aliased destination is owned once: the resources set closes it exactly once
		assertThat(sink.closes.get()).isEqualTo(1);
	}

	@Test
	void channelsSharingOneDestinationEmitAtomicBlocks() throws Exception {
		var sink = new RecordingWriter();
		var transport = StreamTransport.builder(new TrackingStringReader("x"), sink, sink).build();

		var start = new CountDownLatch(1);
		var done = new CountDownLatch(2);
		var failures = new AtomicInteger();
		emit(transport.responses(), "R".repeat(6), 20, start, done, failures);
		emit(transport.messages(), "E".repeat(6), 20, start, done, failures);
		start.countDown();
		done.await();

		assertThat(failures.get()).isZero();
		var lines = List.of(sink.buffer.toString().split("\n"));
		assertThat(lines).hasSize(40);
		assertThat(lines).allSatisfy(line -> {
			assertThat(line).hasSize(6);
			assertThat(line.chars().distinct().count()).as("line not torn: %s".formatted(line)).isEqualTo(1);
		});
		transport.close();
	}

	private static void emit(LineWriter writer, String block, int count,
			CountDownLatch start, CountDownLatch done, AtomicInteger failures)
	{
		Thread.startVirtualThread(() -> {
			try {
				start.await();
				for (int i = 0; i < count; i++) {
					writer.writeln(block);
				}
			} catch (Throwable t) {
				failures.incrementAndGet();
			} finally {
				done.countDown();
			}
		});
	}

	@Test
	void stopEndsInputDiscardingEverythingAfterTheStoppingCommand() throws Exception {
		var transport = StreamTransport.builder(new TrackingStringReader("genmove b\nquit\nplay B Q16\n"),
				new RecordingWriter(), new RecordingWriter()).build();
		assertThat(transport.commands().readLine()).isEqualTo("genmove b");
		transport.stop();
		transport.stop(); // idempotent
		assertThat(transport.stopped()).isTrue();
		// "quit" was already in the input buffer - the session is over, it is not executed
		assertThat(transport.commands().readLine()).isNull();
	}

	@Test
	void closeImpliesStopAndFlushButKeepsProcessStreams() throws Exception {
		var out = new RecordingWriter();
		var err = new RecordingWriter();
		var in = new TrackingStringReader("x");
		var transport = StreamTransport.builder(in, out, err)
				.keepInputOpen()
				.keepOutputsOpen()
				.build();

		transport.responses().writeln("=1 ok");
		transport.close();

		assertThat(transport.stopped()).isTrue();
		assertThat(out.buffer.toString()).isEqualTo("=1 ok\n");
		assertThat(out.closed).as("process stream must not be closed").isFalse();
		assertThat(err.closed).as("process stream must not be closed").isFalse();
		assertThat(in.closed).as("process stdin must not be closed").isFalse();
		// close() implies stop(): reading on and after close yields EOF (null),
		// never IOException("Stream closed") - see StoppableCommandReader
		assertThat(transport.commands().readLine())
				.as("reading after close/stop returns null (EOF)")
				.isNull();
		assertThatThrownBy(() -> transport.responses().writeln("late"))
				.as("writing after close is rejected")
				.isInstanceOf(RejectedExecutionException.class);
	}

	@Test
	void closeClosesSeparateDestinationsAndResourcesAndIsIdempotent() throws Exception {
		var out = new RecordingWriter();
		var err = new RecordingWriter();
		var in = new TrackingStringReader("x");
		var resourcesClosed = new AtomicInteger();
		Closeable resource = () -> resourcesClosed.incrementAndGet();

		var transport = StreamTransport.builder(in, out, err)
				.resource(resource)
				.build();

		assertThat(transport.messages()).as("distinct destinations stay distinct channels")
				.isNotSameAs(transport.responses());
		// executor-style framing: the response block ends with its own '\n' and the writer
		// appends exactly one more - together they are the blank line of spec §2.6
		transport.responses().writeln("=1 ok\n");
		transport.messages().writeln("gogui-gfx: CLEAR");
		transport.close();
		transport.close(); // idempotent - resource must not close twice

		assertThat(out.buffer.toString()).isEqualTo("=1 ok\n\n");
		assertThat(out.closed).isTrue();
		// boundary rule: the message line got its terminator
		assertThat(err.buffer.toString()).isEqualTo("gogui-gfx: CLEAR\n");
		assertThat(err.closed).isTrue();
		assertThat(in.closed).isTrue();
		assertThat(resourcesClosed.get()).isEqualTo(1);
	}

	@Test
	void stdioMapsMessagesToStderr() {
		var stdio = StdioTransport.stdio();
		assertThat(stdio.messages()).as("messages go to STDERR, not onto the response stream")
				.isNotSameAs(stdio.responses());
		stdio.close();
		assertThat(stdio.stopped()).isTrue();
	}

}
