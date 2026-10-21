package cz.doleckovi.piskvorky.gtp.transport;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

/**
 * {@link LineWriter} serializing whole blocks onto a shared lock and guaranteeing the
 * line-boundary rules of the channel contract:
 *
 * <ul>
 *   <li>the entire argument (including any embedded newlines) is written and flushed
 *       under a single monitor, so concurrent writers on this channel &mdash; or on
 *       the other output channel when both were built from the same destination
 *       &mdash; cannot interleave;</li>
 *   <li>exactly one {@code '\n'} is appended to every call, so the call always ends a
 *       line and the destination is left on a clean line boundary; {@code '\n'} inside
 *       the text separates its lines, and empty text emits a blank line;</li>
 *   <li>{@link #close()} flushes once (idempotent) and rejects later writes; closing
 *       the destination itself is <em>not</em> this class's job &mdash; raw streams are
 *       closed by {@link StreamTransport} as the resources the transport owns (see
 *       {@link StreamTransportBuilder}).</li>
 * </ul>
 *
 * <p>The monitor <em>is</em> the destination {@link Writer}: channels sharing one
 * destination are serialized automatically, distinct destinations never block each
 * other. A slow reading peer can stall {@link #writeln} through the {@code flush()}
 * &mdash; that is intended backpressure, not a defect.
 */
final class LockingLineWriter implements LineWriter, Closeable {

	private final Writer delegate;
	private boolean closed;

	LockingLineWriter(Writer delegate) {
		this.delegate = Objects.requireNonNull(delegate, "delegate");
	}

	@Override
	public void writeln(String text) throws RejectedExecutionException, IOException {
		Objects.requireNonNull(text, "text");
		synchronized (delegate) {
			if (closed) {
				throw new RejectedExecutionException("transport output already closed");
			}
			delegate.write(text);
			delegate.write('\n');
			delegate.flush();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (delegate) {
			if (closed) {
				return;
			}
			closed = true;
			delegate.flush();
		}
	}

}
