package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.ActionFactory;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandAction;
import cz.doleckovi.piskvorky.gtp.command.CommandFactory;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * {@link CommandParser} implementation driven by the ANTLR {@code GTP} grammar.
 *
 * <p>A fresh lexer and parser are built per call: the generated ATN, decision DFAs
 * and prediction contexts are static class-level caches, so reuse would save
 * allocations only &mdash; while fresh instances give per-command error accounting
 * and no lexer-mode state leaking across lines and re-entrancy for free. Do not
 * "optimize" this to instance reuse.
 *
 * <p>Besides running the grammar, the instance maps a parsed tree onto Actions by
 * dispatching on the labeled alternative context subclasses (see
 * {@link #build(GTPParser.LineContext, CharStream)}).
 *
 * <p>The instance counts command lines: error positions report line numbers relative
 * to the whole input stream (via {@code Lexer.setLine}), so a syntax error on the
 * fifth command line reads {@code ... at 5:col}, not {@code 1:col}.
 *
 * <p>Instances are not thread-safe; a GTP session is single-threaded by design (see
 * transport {@code package-info.java}).
 */
public class AntlrCommandParser implements CommandParser {

	private final CommandFactory commandFactory;
	private final ActionFactory actionFactory;
	private int lineNumber = 1;

	public AntlrCommandParser(CommandFactory commandFactory, ActionFactory actionFactory) {
		this.commandFactory = commandFactory;
		this.actionFactory = actionFactory;
	}

	@Override
	public Action parse(String commandLine) {
		Objects.requireNonNull(commandLine, "commandLine");
		// CommandReader strips the terminator; grammar alternatives require EOL
		var errors = new CollectingErrorListener();
		var input = CharStreams.fromString(commandLine + "\n");
		var lexer = new GTPLexer(input);
		// number errors against the whole stream, not per line
		lexer.setLine(lineNumber);
		lexer.setCharPositionInLine(0);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errors);
		var parser = new GTPParser(new CommonTokenStream(lexer));
		parser.removeErrorListeners();
		parser.addErrorListener(errors);
		var tree = parser.line();
		// lexer.getLine() has advanced past every '\n' actually consumed
		lineNumber = lexer.getLine();
		var id = tree instanceof GTPParser.CommandLineContext c && c.id != null ? c.id.getText() : null;
		var failure = errorOf(errors, parser);
		if (failure != null)
			// id known even for malformed lines is echoed in the response (spec §3.3)
			return actionFactory.createSyntaxErrorAction(id, failure);
		try {
			return build(tree, input);
		} catch (RuntimeException e) {
			var message = e.getMessage();
			return actionFactory.createSyntaxErrorAction(id, message != null ? message : e.toString());
		}
	}

	/**
	 * Builds an Action out of a parsed tree. One typed method per grammar rule
	 * dispatching on the labeled alternative context subclasses - no visitor/listener
	 * machinery and no parser attributes. Unknown context subclasses (a grammar
	 * alternative added without its builder arm) fail loudly instead of silently
	 * misbehaving.
	 */
	private Action build(GTPParser.LineContext ctx, CharStream input) {
		return switch (ctx) {
			case GTPParser.CommandLineContext c ->
					CommandAction.of(c.id == null ? null : c.id.getText(),
							build(c.command(), input));
			case GTPParser.InterruptLineContext ignored -> actionFactory.createInterruptAction();
			case GTPParser.EmptyLineContext ignored -> actionFactory.createNoOpAction();
			default -> throw new IllegalStateException("Unhandled line alternative " + ctx.getClass().getSimpleName());
		};
	}

	private Command build(GTPParser.CommandContext ctx, CharStream input) {
		return switch (ctx) {
			case GTPParser.SimpleCommandContext c ->
					commandFactory.createSimpleCommand(c.COMMAND().getText());
			case GTPParser.NumberCommandContext c ->
					commandFactory.createNumberCommand(c.COMMAND().getText(), Integer.parseInt(c.INTEGER().getText()));
			case GTPParser.VertexCommandContext c ->
					commandFactory.createVertexCommand(c.COMMAND().getText(), Vertex.parse(c.VERTEX().getText()));
			case GTPParser.SideCommandContext c ->
					commandFactory.createSideCommand(c.COMMAND().getText(), build(c.side()));
			case GTPParser.MoveCommandContext c ->
					commandFactory.createMoveCommand(c.COMMAND().getText(), build(c.moves()));
			case GTPParser.KVPCommandContext c ->
					commandFactory.createKVPCommand(c.COMMAND().getText(), c.KEY().getText(), build(c.text(), input));
			case GTPParser.TextCommandContext c ->
					commandFactory.createTextCommand(c.COMMAND().getText(), build(c.text(), input));
			default -> throw new IllegalStateException("Unhandled command alternative " + ctx.getClass().getSimpleName());
		};
	}

	private List<Move> build(GTPParser.MovesContext ctx) {
		return switch (ctx) {
			case GTPParser.CreateMovesContext c -> new ArrayList<>(List.of(build(c.move())));
			case GTPParser.AddMoveContext c -> {
				var moves = build(c.moves());
				moves.add(build(c.move()));
				yield moves;
			}
			default -> throw new IllegalStateException("Unhandled moves alternative " + ctx.getClass().getSimpleName());
		};
	}

	private Move build(GTPParser.MoveContext ctx) {
		return switch (ctx) {
			case GTPParser.NormalMoveContext c -> new Move(build(c.side()).stone, Vertex.parse(c.VERTEX().getText()));
			case GTPParser.BlockMoveContext c -> new Move(Stone.BLOCK, Vertex.parse(c.VERTEX().getText()));
			default -> throw new IllegalStateException("Unhandled move alternative " + ctx.getClass().getSimpleName());
		};
	}

	private Side build(GTPParser.SideContext ctx) {
		return switch (ctx) {
			case GTPParser.WhiteColorContext ignored -> Side.WHITE;
			case GTPParser.BlackColorContext ignored -> Side.BLACK;
			default -> throw new IllegalStateException("Unhandled side alternative " + ctx.getClass().getSimpleName());
		};
	}

	/**
	 * Recovers the text argument verbatim from the input characters, spanning from the
	 * first to the last {@code WORD} token. Whitespace runs <em>between</em> words keep
	 * their original size (spaces, tabs); whitespace and comments are still excluded at
	 * the edges (before the first and after the last word).
	 *
	 * <p>Relies on the input being fully buffered.
	 */
	private String build(GTPParser.TextContext ctx, CharStream input) {
		return switch (ctx) {
			case GTPParser.AddWordsContext ignored -> input.getText(Interval.of(
					ctx.getStart().getStartIndex(),
					ctx.getStop().getStopIndex()));
			default -> throw new IllegalStateException("Unhandled text alternative " + ctx.getClass().getSimpleName());
		};
	}

	private static String errorOf(CollectingErrorListener errors, GTPParser parser) {
		if (errors.hasErrors())
			return errors.firstError();
		if (parser.getCurrentToken() != null && parser.getCurrentToken().getType() != Token.EOF)
			// input contained more than one line - refuse silently consuming the rest
			return "unexpected trailing content at %d:%d".formatted(
					parser.getCurrentToken().getLine(), parser.getCurrentToken().getCharPositionInLine());
		return null;
	}

}
