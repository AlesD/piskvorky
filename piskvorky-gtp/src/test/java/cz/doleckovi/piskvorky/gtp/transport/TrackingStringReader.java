package cz.doleckovi.piskvorky.gtp.transport;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Test double: {@link Reader} over fixed content that records {@code close()}.
 * Shared by the transport package tests.
 */
final class TrackingStringReader extends Reader {

	private final StringReader delegate;
	volatile boolean closed;

	TrackingStringReader(String content) {
		this.delegate = new StringReader(content);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return delegate.read(cbuf, off, len);
	}

	@Override
	public void close() {
		closed = true;
	}

}
