package cz.doleckovi.piskvorky.gtp.transport;

import java.io.IOException;

/**
 * Write side of a {@link GtpTransport}: atomic emission of one or more whole lines.
 *
 * <p>Both output channels ({@link GtpTransport#responses()} and
 * {@link GtpTransport#messages()}) are line-oriented, but callers naturally hold
 * different amounts of text:
 * <ul>
 *   <li>the response-sending thread receives a command's <em>whole result</em> from
 *       {@code Command.call()} and forwards it 1:1 &mdash; a multi-line block;</li>
 *   <li>a command emits messages (e.g. GoGui {@code gogui-gfx:} live graphics) line
 *       by line while it runs.</li>
 * </ul>
 * One method covers both: the argument may be a single line or a block containing
 * embedded newlines.
 */
@FunctionalInterface
public interface LineWriter {

	/**
	 * Emits {@code text} &mdash; one or more complete lines &mdash; atomically to the
	 * destination, terminates the last line with {@code '\n'} and flushes.
	 *
	 * <p>Atomicity: the transport holds the destination lock for the whole text, so
	 * nothing written concurrently on the same channel &mdash; or on the other output
	 * channel, if the two share one destination &mdash; can tear into the block. Line
	 * boundary: exactly one {@code '\n'} is appended, whatever the text ends with;
	 * use {@code '\n'} inside the text to separate its lines, and an empty
	 * {@code text} to emit a blank line. Assembling the <em>content</em> (the
	 * {@code =}/{@code ?} framing, id echo, the terminating blank line of spec
	 * &sect;2.6) remains the caller's job; a redundant empty line is harmless, since
	 * the controller must ignore empty lines occurring outside a response
	 * (spec &sect;2.10).
	 *
	 * @param text one or more lines; {@code ""} emits a single blank line
	 * @throws IOException  if the underlying output fails or the transport is closed
	 * @throws NullPointerException if {@code text} is {@code null}
	 */
	void writeln(String text) throws IOException;

}
