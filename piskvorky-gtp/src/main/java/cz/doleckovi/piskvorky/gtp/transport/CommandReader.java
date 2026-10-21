package cz.doleckovi.piskvorky.gtp.transport;

import java.io.IOException;

/**
 * Read side of a {@link GtpTransport}: the sequence of incoming GTP command lines.
 *
 * <p>A GTP command is exactly one line (spec &sect;2.5), so the line is the natural
 * unit of the input channel. The transport strips the line terminator; protocol
 * preprocessing (comment removal, CR/HT handling, discarding empty lines per
 * spec &sect;3.1) remains the job of the parsing layer above.
 */
@FunctionalInterface
public interface CommandReader {

	/**
	 * Reads the next command line, without its terminator.
	 *
	 * <p>Returns {@code null} at end of input &mdash; the peer/file closed, or the
	 * transport was {@linkplain GtpTransport#stop() stopped}. After a {@code stop()}
	 * nothing further is delivered: input that arrived past the command which ended the
	 * session must not be executed (spec &sect;6.3.1). When the input itself ends
	 * mid-line, already-arrived content that lacks a terminator may be delivered as a
	 * final trailing line (disconnect race); an empty line is a legitimate {@code ""}
	 * result, not EOF.
	 *
	 * @return the next command line, or {@code null} at EOF
	 * @throws IOException if the underlying input fails
	 */
	String readLine() throws IOException;

}
