package cz.doleckovi.piskvorky.gtp.transport;

import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test double: unsynchronized {@link Writer} appending char-by-char with a yield to
 * provoke interleaving. Shared by the transport package tests.
 */
final class RecordingWriter extends Writer {

	final StringBuilder buffer = new StringBuilder();
	final AtomicReference<Throwable> error = new AtomicReference<>();
	final AtomicInteger flushes = new AtomicInteger();
	final AtomicInteger closes = new AtomicInteger();
	volatile boolean closed;

	@Override
	public void write(char[] cbuf, int off, int len) {
		try {
			for (int i = off; i < off + len; i++) {
				buffer.append(cbuf[i]);
				Thread.yield();
			}
		} catch (RuntimeException | Error e) {
			error.set(e);
			throw e;
		}
	}

	@Override
	public void write(int c) {
		buffer.append((char) c);
	}

	@Override
	public void flush() {
		flushes.incrementAndGet();
	}

	@Override
	public void close() {
		closed = true;
		closes.incrementAndGet();
	}

}
