/**
 * Transport abstraction of the Go Text Protocol: the bidirectional, line-oriented
 * channel between this engine and a <em>single</em> GTP peer.
 *
 * <p>This package is <strong>pure {@code java.io}</strong> &mdash; no Spring, no ANTLR,
 * no game logic. Feeding {@link GtpTransport#commands()} into the parser and routing
 * command output into {@link GtpTransport#responses()} / {@link GtpTransport#messages()}
 * is the responsibility of the wiring layers, not of the transport.
 *
 * <h1 class="font-bold">Channels</h1>
 * <ul>
 *   <li>{@link GtpTransport#commands()} &mdash; a {@link CommandReader} producing
 *       incoming GTP commands, one per line (GTP v2 spec &sect;2.5);</li>
 *   <li>{@link GtpTransport#responses()} &mdash; a {@link LineWriter} for framed
 *       command results ({@code = [id] ...} / {@code ? [id] ...}), written by the
 *       single ordered response-sending thread (spec &sect;2.8 requires responses in
 *       command order);</li>
 *   <li>{@link GtpTransport#messages()} &mdash; a {@link LineWriter} for line-oriented
 *       information a command emits <em>while it is running</em> (e.g. GoGui
 *       {@code gogui-gfx:} live graphics), written by the command-executing thread.</li>
 * </ul>
 * Raw {@code Reader}/{@code Writer} streams exist only inside
 * {@link StreamTransportBuilder} (where sockets, files and charset decoding plug in)
 * &mdash; never on the transport surface.
 *
 * <h1 class="font-bold">Defaults</h1>
 * Each channel has a natural destination for interactive peers: commands from STDIN,
 * responses to STDOUT, messages to STDERR &mdash; exactly what GoGui expects for live
 * graphics (see {@code docs/gogui/live-graphics.md}). Merging the two output streams is
 * the client's business (shell {@code 2>&1}), not a mode of the transport: machine
 * clients must not be fed a single combined stream, because multi-line response blocks
 * and message lines then become hard to attribute apart. A transport has no notion of
 * &mdquo;merged&rdquo; channels &mdash; if it is built from the <em>same</em> writer for
 * both output channels, they are served by one channel object locking on that shared
 * destination and stay block-atomic.
 *
 * <h1 class="font-bold">Units: lines in, blocks out</h1>
 * Input granularity is the <em>line</em>: {@link CommandReader#readLine()} returns one
 * command without its terminator. Protocol preprocessing (CR/comment/HT handling and
 * dropping empty lines, spec &sect;3.1) is the parsing layer's job, not the
 * transport's.
 *
 * <p>Output granularity is the <em>block of one or more complete lines</em>:
 * {@link LineWriter#writeln(String)} accepts text that may contain embedded newlines,
 * holds the destination lock for the whole call, appends exactly one {@code '\n'} and
 * flushes afterwards &mdash; so a multi-line response can never be torn apart by a
 * concurrent write on the same destination, and every call leaves the destination on a
 * clean line boundary. Assembling the <em>content</em> stays the caller's job: the
 * {@code =}/{@code ?} framing, the id echo and the terminating blank line of spec
 * &sect;2.6 are produced above the transport (commands return their whole result from
 * {@code call()} and the sender thread forwards it 1:1; message lines are typically
 * written one per call). A redundant empty line costs nothing: the controller must
 * ignore empty lines occurring outside a response (spec &sect;2.10).
 *
 * <h1 class="font-bold">No cross-channel ordering</h1>
 * Responses and messages come from different threads and the transport provides
 * <em>no</em> ordering guarantee between them: a client reading both may legitimately
 * see messages of command <em>N+1</em> before the response of
 * command <em>N</em>. This matches GTP &sect;2.8 and the race GoGui explicitly
 * documents; command-level mitigations (e.g. {@code gogui-gfx: CLEAR} before the
 * response ends) live outside this package.
 *
 * <h1 class="font-bold">Stop semantics</h1>
 * {@link GtpTransport#stop()} ends the command input: from then on {@code readLine()}
 * yields {@code null} and anything still pending in the input buffer is discarded.
 * Session-end is derived from the protocol rather than from timing &mdash; spec
 * &sect;6.3.1 makes {@code quit} the last line a conforming engine ever has to read (the
 * session is terminated and the connection closed once its response is sent), so input
 * still buffered when {@code stop()} is set belongs to no session, and the input loop
 * ends by <em>dispatching</em> that command, not by observing EOF. No wake-up of an
 * already blocked read is therefore promised (a terminal read offers none); where the
 * source supports it a transport may still provide it (TCP {@code shutdownInput()}
 * does). Real end of input is EOF like any other disconnect, a trailing unterminated
 * fragment possibly included. See {@code docs/gtp/gtp2-spec-draft2.md} &sect;6.3.1 and
 * &sect;2.8: the protocol imposes no synchronization requirements between controller and
 * engine, so a transport can neither wake a read nor infer a finished peer from timing.
 *
 * <h1 class="font-bold">Character set</h1>
 * GTP messages are 8-bit character sequences (spec &sect;2.1): US-ASCII for
 * standardized syntax, characters 128&ndash;255 only in comments and private
 * extensions, with <em>no preferred character set</em>. Standardized syntax is
 * byte-identical under UTF-8, so the choice only affects that non-ASCII remainder.
 * A transport decodes/encodes its streams at construction; {@link StdioTransport}
 * defaults each stream to the encoding the JVM reports for it ({@code stdin.encoding},
 * {@code stdout.encoding}, {@code stderr.encoding}) with UTF-8 as final fallback.
 * {@code -Dpiskvorky.gtp.encoding} overrides all streams &mdash; e.g.
 * {@link java.nio.charset.StandardCharsets#ISO_8859_1}, whose byte-transparent mapping
 * passes arbitrary 8-bit content through unharmed.
 *
 * <h1 class="font-bold">Buildings</h1>
 * {@link StreamTransport} is the generic implementation composing raw streams supplied
 * via {@link StreamTransportBuilder} with the behaviors above, using two internal
 * adapters: {@link StoppableCommandReader} (line buffering, EOF-on-stop) and
 * {@link LockingLineWriter} (atomic block emission, flush-on-close). Whether the raw
 * streams themselves get closed is an ownership question settled by the builder: they
 * are owned &mdash; and closed as resources, once each &mdash; unless the factory
 * declares them borrowed via {@code keepInputOpen()} / {@code keepOutputsOpen()}
 * (STDIO must). {@link StdioTransport} is the primary factory (STDIN/STDOUT/STDERR).
 * TCP and file-scenario factories build on the same builder.
 */
package cz.doleckovi.piskvorky.gtp.transport;
