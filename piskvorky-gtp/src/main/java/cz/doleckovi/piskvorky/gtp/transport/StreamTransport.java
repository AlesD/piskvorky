package cz.doleckovi.piskvorky.gtp.transport;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Generic {@link GtpTransport} implementation composing supplied raw character streams
 * with the behaviors defined in the package documentation: EOF-on-stop line input
 * ({@link StoppableCommandReader}), atomically serialized block outputs
 * ({@link LockingLineWriter}). {@link #close()} closes everything the transport owns:
 * the two channel objects (which flush but never touch their destination's lifecycle)
 * and the raw streams the {@link StreamTransportBuilder} registered as resources.
 *
 * <p>Raw {@link Reader}/{@link Writer} streams are accepted only by the
 * package-private constructor and {@link StreamTransportBuilder} &mdash; the place
 * where sockets, files and charset decoding are wired &mdash; never on the public
 * transport surface.
 *
 * <p>Instances are created through {@link StdioTransport} or via
 * {@link #builder(Reader, Writer, Writer)}.
 */
public final class StreamTransport implements GtpTransport {

	/**
	 * Opens a builder over the three channel destinations; all are required
	 * (non-{@code null}) and, by default, owned &mdash; closing the transport closes
	 * them. Passing the same {@code Writer} for responses and messages builds both
	 * output channels over one destination; {@code keepInputOpen()} /
	 * {@code keepOutputsOpen()} declare streams borrowed instead.
	 */
	public static StreamTransportBuilder builder(Reader commands, Writer responses, Writer messages) {
		return new StreamTransportBuilder(commands, responses, messages);
	}

	private static IOException closeQuietly(Closeable closeable, IOException failure) {
		if (closeable == null) {
			return failure;
		}
		try {
			closeable.close();
			return failure;
		} catch (IOException e) {
			if (failure == null) {
				return e;
			}
			failure.addSuppressed(e);
			return failure;
		}
	}

	private final AtomicBoolean stopped = new AtomicBoolean(false);
	private final AtomicBoolean closed = new AtomicBoolean(false);

	private final StoppableCommandReader commands;
	private final LockingLineWriter responses;
	private final LockingLineWriter messages;
	private final List<Closeable> closeables;

	StreamTransport(Reader commands, Writer responses, Writer messages, List<Closeable> resources) {
		this.commands = new StoppableCommandReader(commands, stopped::get);
		this.responses = new LockingLineWriter(responses);
		// same destination writer -> one channel object: one latch, one monitor, one close
		this.messages = messages == responses ? this.responses : new LockingLineWriter(messages);

		// close in one ordered sweep: channel objects flush first, then owned streams;
		// aliased channels are the same instance, so the set closes everything exactly once
		var ordered = new LinkedHashSet<Closeable>();
		ordered.add(this.responses);
		ordered.add(this.messages);
		ordered.addAll(resources);
		this.closeables = List.copyOf(ordered);
	}

	@Override
	public CommandReader commands() {
		return commands;
	}

	@Override
	public LineWriter responses() {
		return responses;
	}

	@Override
	public LineWriter messages() {
		return messages;
	}

	@Override
	public boolean stopped() {
		return stopped.get();
	}

	@Override
	public void stop() {
		stopped.set(true);
	}

	@Override
	public void close() {
		stop();
		if (!closed.compareAndSet(false, true)) {
			return;
		}
		IOException failure = null;
		for (Closeable closeable : closeables) {
			failure = closeQuietly(closeable, failure);
		}
		if (failure != null) {
			throw new UncheckedIOException(failure);
		}
	}

}
