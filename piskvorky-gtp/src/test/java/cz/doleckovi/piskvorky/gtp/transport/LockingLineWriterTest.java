package cz.doleckovi.piskvorky.gtp.transport;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

class LockingLineWriterTest implements WithAssertions {

	@Test
	void appendsExactlyOneNewlineToEveryBlock() throws IOException {
		var base = new RecordingWriter();
		var writer = new LockingLineWriter(base);
		writer.writeln("x");
		writer.writeln("a\n");
		writer.writeln("b\nc\n");
		writer.writeln("");
		// every call ends exactly one line: the caller's own '\n' becomes a blank line
		assertThat(base.buffer.toString()).isEqualTo("x\na\n\nb\nc\n\n\n");
	}

	@Test
	void flushesAfterEveryWrite() throws IOException {
		var base = new RecordingWriter();
		var writer = new LockingLineWriter(base);
		writer.writeln("one");
		writer.writeln("two");
		assertThat(base.flushes.get()).isEqualTo(2);
	}

	@Test
	void wholeBlocksAreAtomicOnSharedDestination() throws Exception {
		var base = new RecordingWriter();
		// two channel objects over one destination: the destination is the monitor
		var responses = new LockingLineWriter(base);
		var messages = new LockingLineWriter(base);

		var start = new CountDownLatch(1);
		var done = new CountDownLatch(2);
		var failures = new AtomicInteger();
		emit(responses, "R".repeat(6), 20, start, done, failures);
		emit(messages, "E".repeat(6), 20, start, done, failures);
		start.countDown();
		done.await();

		assertThat(failures.get()).isZero();
		assertThat(base.error.get()).isNull();
		// every emitted line must be a homogeneous, whole block of 6 chars
		var lines = List.of(base.buffer.toString().split("\n"));
		assertThat(lines).hasSize(40);
		assertThat(lines).allSatisfy(line -> {
			assertThat(line).hasSize(6);
			assertThat(line.chars().distinct().count()).as("line not torn: %s".formatted(line)).isEqualTo(1);
		});
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
	void closeFlushesIdempotentlyAndRejectsWritesAfterwards() throws IOException {
		var base = new RecordingWriter();
		var writer = new LockingLineWriter(base);
		writer.writeln("=2 ok");
		writer.close();
		writer.close(); // idempotent
		assertThat(base.flushes.get()).isGreaterThanOrEqualTo(2);
		assertThat(base.closed).as("closing the destination is the transport's resource job")
				.isFalse();
		assertThatThrownBy(() -> writer.writeln("late")).isInstanceOf(RejectedExecutionException.class);
	}

}
