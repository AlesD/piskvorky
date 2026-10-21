package cz.doleckovi.piskvorky.gtp.transport;

/**
 * The bidirectional channel between this engine and a <em>single</em> GTP peer
 * (an interactive front end, a network client, or a game scenario file).
 * Multi-session concerns live outside this abstraction.
 *
 * <p>Transport I/O is line-oriented in both directions: commands arrive as lines
 * through a {@link CommandReader}, both output channels are {@link LineWriter}s
 * emitting atomic blocks of one or more lines. The architecture of the three
 * channels, their defaults, ordering rules, stop semantics and charset policy is
 * described in the <a href="package-summary.html">package documentation</a>.
 */
public interface GtpTransport extends AutoCloseable {

	/**
	 * Incoming GTP command lines; yields EOF after {@link #stop()} or when the
	 * peer/file closes.
	 */
	CommandReader commands();

	/**
	 * Framed GTP command results ({@code =}/{@code ?} response blocks).
	 */
	LineWriter responses();

	/**
	 * Line-oriented extra information emitted by commands while executing
	 * (STDERR by default; shares its destination with {@link #responses()} only if the
	 * transport was built from the very same writer).
	 */
	LineWriter messages();

	/**
	 * Whether the command input has been ended by {@link #stop()} (or {@link #close()}).
	 */
	boolean stopped();

	/**
	 * Signal end of command input: from now on {@link CommandReader#readLine()}
	 * yields EOF. Idempotent; does not promise to unblock an already blocked read
	 * (see package documentation).
	 */
	void stop();

	/**
	 * End the session: implies {@link #stop()}, flushes both output channels and
	 * closes underlying resources according to the transport's policy (STDIO never
	 * closes the process streams). Writing to an output channel after close fails
	 * with {@link java.io.IOException}. Idempotent.
	 */
	@Override
	void close();

}
