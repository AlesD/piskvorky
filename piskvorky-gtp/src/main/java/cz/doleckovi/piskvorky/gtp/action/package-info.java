/** Classes used to represent data used in GTP processing.
 *
 * <p>{@link Action}s represent units of execution. They operate on
 * {@link Context} which represents state of {@link Session} and produce
 * {@link Result}s. There is dedicated instance for every action which allows
 * them </strong>
 * {@link Action#execute(Context) execute(context)} returns a {@link Result}:</p>
 * <ul>
 *   <li>{@link Result.Success} / {@link Result.Failure} — the wire block is
 *       produced by {@link Result#format()}, ready to be handed 1:1 to
 *       {@link LineWriter#writeln} on the transport's
 *       {@link GtpTransport#responses() responses} channel.</li>
 *   <li>{@link Result#IGNORED} when the action produces <em>no output at all</em>
 *       — ignored lines (empty, whitespace-only, comment-only; GTP spec &sect;3.1)
 *       and the interrupt line (spec &sect;6.3.2). This is why these map to actions
 *       and not to no-answer commands: every command result is framed as a response,
 *       which would leak a spurious {@code =} for a line that must be silent.</li>
 * </ul>
 *
 * <p><strong>Framing</strong> (spec &sect;2.6, provided by {@link Result#format()}):</p>
 * <ul>
 *     <li>The wire block is {@code "=" | "?"}, optionally {@code " " + id},
 *         optionally {@code " " + answer-text}</li>
 *     <li>The block may contain embedded {@code '\n'} - in that case only
 *         the first line carries the marker.</li>
 *     <li>The block is terminated by {@code '\n'}. The line writer appends
 *         one more {@code '\n'}, which forms the blank line terminating
 *         every response.</li>
 *     <li>Answer text is inserted verbatim; text ending with its own newline
 *         merely yields an extra blank line, which controllers must ignore
 *         (spec &sect;2.10).</li>
 * </ul></p>
 *
 * <p><strong>Failure is output, not an exception.</strong> A failing action
 * produces a {@link Result.Failure} (message and echoed ID when known — spec
 * &sect;3.3 requires rejecting a malformed line by its ID too).
 * {@code execute(...)} declares no checked exceptions.</p>
 *
 * <p><strong>Vocabulary.</strong> {@link Context} is the session facet an
 * action executes against - the transport's {@link GtpTransport#messages
 * messages} channel plus the session's current board and game. {@link Session}
 * implements it (one instance per session, game access confined to the action
 * worker). A GTP <em>command</em> is the body a parsed command line denotes; the
 * {@code Command}/{@code CommandFactory}/{@code CommandException} contracts and the
 * {@link cz.doleckovi.piskvorky.gtp.command.CommandAction} adapter wrapping a
 * command into an {@link Action} (owning the response ID and the framing into a
 * {@link Result}) live in {@link cz.doleckovi.piskvorky.gtp.command}.</p>
 */
package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.gtp.session.Session;
import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;