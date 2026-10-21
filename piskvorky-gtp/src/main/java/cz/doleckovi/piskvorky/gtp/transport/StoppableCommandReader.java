package cz.doleckovi.piskvorky.gtp.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.BooleanSupplier;

/**
 * {@link CommandReader} buffering a {@link Reader} into complete lines and ending that
 * input as soon as the transport is stopped.
 *
 * <p>Line terminators are stripped. Once the {@code stopped} flag is observed,
 * {@link #readLine()} returns {@code null} and everything still pending in the buffer is
 * discarded: per spec &sect;6.3.1 {@code quit} is the last line a conforming engine ever
 * has to read, so input still buffered behind it belongs to no session and is not
 * executed. A trailing fragment of an unterminated line is still delivered when the
 * <em>input itself</em> ends, which mirrors what a mid-line disconnect looks like.
 *
 * <p>This adapter has no lifecycle at all: a {@link BufferedReader} holds no OS
 * resource, so it is never closed (and closing it would only poison the buffer). The raw
 * source is closed by {@link StreamTransport} only when the transport owns it &mdash;
 * see {@link StreamTransportBuilder#keepInputOpen()}.
 *
 * <p>A source that is already a {@link BufferedReader} is used as supplied &mdash;
 * stacking a second buffer would only copy characters the caller already has buffered.
 */
final class StoppableCommandReader implements CommandReader {

	private final BufferedReader delegate;
	private final BooleanSupplier stopped;

	StoppableCommandReader(Reader source, BooleanSupplier stopped) {
		this.delegate = source instanceof BufferedReader buffered
				? buffered
				: new BufferedReader(source);
		this.stopped = stopped;
	}

	@Override
	public String readLine() throws IOException {
		return stopped.getAsBoolean() ? null : delegate.readLine();
	}

}
