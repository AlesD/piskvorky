package cz.doleckovi.piskvorky.gtp.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Test double: a {@link BufferedReader} that counts its own {@code readLine()} calls, so a
 * test can tell whether {@link StoppableCommandReader} delegated to the buffer it was
 * given or stacked another one on top of it. Shared by the transport package tests.
 */
final class CountingBufferedReader extends BufferedReader {

	private int lineReads;

	CountingBufferedReader(Reader source) {
		super(source);
	}

	@Override
	public String readLine() throws IOException {
		lineReads++;
		return super.readLine();
	}

	int lineReads() {
		return lineReads;
	}

}
