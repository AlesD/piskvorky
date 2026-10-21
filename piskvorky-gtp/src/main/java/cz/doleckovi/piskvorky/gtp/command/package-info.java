/**
 * GTP command bodies and their creation contracts.
 *
 * <p>{@link cz.doleckovi.piskvorky.gtp.command.Command} is the body a parsed command
 * line denotes: it executes against the session's
 * {@link cz.doleckovi.piskvorky.gtp.action.Context} and returns the answer text.
 * {@link cz.doleckovi.piskvorky.gtp.command.CommandFactory} maps a command name and its
 * parsed arguments to such a body; {@link cz.doleckovi.piskvorky.gtp.command.CommandException}
 * reports an execution failure whose message becomes the {@code ?} response.
 *
 * <p>{@link CommandAction} adapts a {@link Command} to the pipeline's
 * {@link cz.doleckovi.piskvorky.gtp.action.Action}: it owns the response ID echoed
 * in the {@link cz.doleckovi.piskvorky.gtp.action.Result} and stamps the command's
 * traits onto both action and result ({@link CancelableCommandAction} being the
 * variant for commands declaring
 * {@link cz.doleckovi.piskvorky.gtp.trait.Cancelable}). {@link CommandAction#of}
 * is the entry point used by parsers.
 *
 * <p>The command bodies in this package implement {@link
 * cz.doleckovi.piskvorky.gtp.command.Command}; they are stateless and reusable.
 * The GoGui command families live in subpackages:
 * {@link cz.doleckovi.piskvorky.gtp.command.gogui} (extensions) and
 * {@link cz.doleckovi.piskvorky.gtp.command.gogui.rules} ({@code gogui-rules_*}),
 * each with its own factory; {@link CompositeCommandFactory} combines the factories
 * into one.
 */
package cz.doleckovi.piskvorky.gtp.command;
