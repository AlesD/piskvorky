/**
 * Parsing of GTP command lines: {@link cz.doleckovi.piskvorky.gtp.parser.CommandParser}
 * turns one command line (as delivered by
 * {@link cz.doleckovi.piskvorky.gtp.transport.CommandReader}, without its terminator)
 * into one {@link cz.doleckovi.piskvorky.gtp.action.Action}.
 *
 * <p>The {@link cz.doleckovi.piskvorky.gtp.parser.CommandParser} contract is
 * implementation-independent: parsing never throws &mdash; blank lines become no-op
 * actions, malformed lines syntax-error actions echoing any recognized id (GTP spec
 * &sect;3.1, &sect;3.3). Command bodies are created through
 * {@link cz.doleckovi.piskvorky.gtp.command.CommandFactory} and wrapped by
 * {@link cz.doleckovi.piskvorky.gtp.command.CommandAction}; line-level actions
 * (interrupt, no-op, syntax error) come from
 * {@link cz.doleckovi.piskvorky.gtp.action.ActionFactory}, so a parser needs no
 * knowledge of which commands the engine supports.
 *
 * <p>{@link cz.doleckovi.piskvorky.gtp.action.Vertex} and
 * {@link cz.doleckovi.piskvorky.gtp.action.Move} are protocol argument data types
 * passed to the command factory.
 *
 * <p>{@link cz.doleckovi.piskvorky.gtp.parser.AntlrCommandParser} is currently the
 * only implementation; the contract itself does not depend on it.
 *
 * <p>Implementations may be stateful (e.g. counting lines for stream-relative error
 * positions): keep one parser instance per GTP session and do not share it across
 * threads - a session is single-threaded by design.
 */
package cz.doleckovi.piskvorky.gtp.parser;
