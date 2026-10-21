package cz.doleckovi.piskvorky.gtp.transport;

import java.io.Closeable;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Fluent builder composing the {@link StreamTransport} from plain character streams.
 *
 * <p>This is the building block every concrete transport factory uses: open the raw
 * streams for all three channels, describe how they should be treated and
 * {@link #build()}. Behavior added on top of the supplied streams (EOF-on-stop, atomic
 * writes) is described in the package documentation; the builder only settles
 * <em>ownership</em> &mdash; what {@link GtpTransport#close()} closes:
 * <ul>
 *   <li>the three channel streams are owned (hence closed) by default;
 *       {@link #keepInputOpen()} / {@link #keepOutputsOpen()} irrevocably mark them
 *       borrowed instead (STDIO must flush but never close the process streams);</li>
 *   <li>{@link #resource(Closeable)} &mdash; registers an additional owned closeable
 *       (e.g. a socket).</li>
 * </ul>
 *
 * <p>Owned streams and registered resources form one set: an object entered twice is
 * closed once, and aliased output channels therefore never double-close their shared
 * destination. {@link GtpTransport#close()} closes channel objects first (flushing
 * them), then this set, in registration order.
 *
 * <p>All three destinations (commands, responses, messages) are required at
 * construction: there is no default for diagnostics, so every factory states
 * explicitly where its message lines go. Supplying the <em>same</em> writer for the two
 * output channels builds one channel object over that destination; the shell
 * ({@code 2>&1}) remains the normal way to combine streams.
 */
public final class StreamTransportBuilder {

	final Reader commands;
	final Writer responses;
	final Writer messages;
	final Set<Closeable> owned = new LinkedHashSet<>();

	StreamTransportBuilder(Reader commands, Writer responses, Writer messages) {
		this.commands = Objects.requireNonNull(commands, "commands");
		this.responses = Objects.requireNonNull(responses, "responses");
		this.messages = Objects.requireNonNull(messages, "messages");
		owned.add(this.commands);
		owned.add(this.responses);
		owned.add(this.messages); // aliased destination: the set keeps one entry
	}

	/** Keeps the commands stream open on {@link GtpTransport#close()} (STDIO must). */
	public StreamTransportBuilder keepInputOpen() {
		owned.remove(commands);
		return this;
	}

	/**
	 * Keeps both output streams open on {@link GtpTransport#close()} (STDIO must);
	 * they are still flushed.
	 */
	public StreamTransportBuilder keepOutputsOpen() {
		owned.remove(responses);
		owned.remove(messages);
		return this;
	}

	/** Registers an additional closeable owned by the transport (e.g. a socket). */
	public StreamTransportBuilder resource(Closeable resource) {
		if (resource != null) {
			owned.add(resource);
		}
		return this;
	}

	public StreamTransport build() {
		return new StreamTransport(commands, responses, messages, List.copyOf(owned));
	}

}
