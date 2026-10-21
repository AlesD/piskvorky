package cz.doleckovi.piskvorky.gtp.transport;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for the primary {@link GtpTransport} &mdash; STDIN / STDOUT / STDERR.
 *
 * <p>Intended for interactive GTP front ends: the main use case of this application is
 * to be played and developed through programs that speak Go Text Protocol instead of
 * building our own GUI.
 *
 * <p>Channel mapping by default:
 * <ul>
 *   <li>{@link GtpTransport#commands()} &mdash; STDIN (terminals deliver complete
 *       lines only, once Enter is pressed)</li>
 *   <li>{@link GtpTransport#responses()} &mdash; STDOUT (pure protocol)</li>
 *   <li>{@link GtpTransport#messages()} &mdash; STDERR, exactly what GoGui expects for
 *       live graphics (see {@code docs/gogui/live-graphics.md}); a client may merge the
 *       streams itself (shell {@code 2>&1})</li>
 * </ul>
 *
 * <p>{@link GtpTransport#close()} flushes the writers but never closes the process
 * streams; {@link GtpTransport#stop()} yields EOF on the next read attempt &mdash; the
 * terminal read itself cannot be woken, and does not need to be: a session ends because
 * {@code quit} is the last line the engine reads (spec &sect;6.3.1), not because
 * {@code stop()} produced EOF.
 *
 * <p>Character sets: standardized GTP syntax is US-ASCII, whose bytes are identical
 * under UTF-8, so the charset choice only affects comments and private extensions
 * (spec &sect;2.1 leaves it unspecified). Each stream defaults to the encoding the JVM
 * reports for it ({@code stdin.encoding}, {@code stdout.encoding},
 * {@code stderr.encoding}) with UTF-8 as final fallback &mdash; the choice a terminal
 * or piped launcher actually uses; {@code -Dpiskvorky.gtp.encoding} overrides all three
 * at once (e.g. {@code ISO-8859-1} for byte-transparent pass-through of an unknown 8-bit
 * encoding). {@link #stdio(Charset)} pins one explicit charset for every stream.
 */
public final class StdioTransport {

	/** System property overriding the transport charset. */
	private static final String CHARSET_PROPERTY = "piskvorky.gtp.encoding";

	private static final Logger LOG = Logger.getLogger(StdioTransport.class.getName());

	/** STDIN in, responses to STDOUT, messages to STDERR; per-stream process encodings, {@code -Dpiskvorky.gtp.encoding} override. */
	public static GtpTransport stdio() {
		return create(stdioCharset("stdin.encoding"), stdioCharset("stdout.encoding"), stdioCharset("stderr.encoding"));
	}

	/** STDIN in, responses to STDOUT, messages to STDERR, one explicit charset for all streams. */
	public static GtpTransport stdio(Charset charset) {
		return create(charset, charset, charset);
	}

	private static GtpTransport create(Charset input, Charset responses, Charset messages) {
		Reader in = new InputStreamReader(System.in, input);
		Writer out = new OutputStreamWriter(System.out, responses);
		Writer err = new OutputStreamWriter(System.err, messages);
		return StreamTransport.builder(in, out, err)
				.keepInputOpen()
				.keepOutputsOpen()
				.build();
	}

	/**
	 * Charset for one process stream: {@code -Dpiskvorky.gtp.encoding} overrides all
	 * streams; otherwise the encoding the JVM reports for that stream
	 * ({@code stdin.encoding} &hellip;), else UTF-8. Unset or invalid values fall back
	 * rather than throw.
	 */
	private static Charset stdioCharset(String jvmProperty) {
		Charset forced = lookup(System.getProperty(CHARSET_PROPERTY));
		if (forced != null) {
			return forced;
		}
		Charset process = lookup(System.getProperty(jvmProperty));
		return process != null ? process : StandardCharsets.UTF_8;
	}

	private static Charset lookup(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		try {
			return Charset.forName(name);
		} catch (RuntimeException e) {
			LOG.log(Level.WARNING, "Unknown charset: " + name, e);
			return null;
		}
	}

	private StdioTransport() {
	}

}
