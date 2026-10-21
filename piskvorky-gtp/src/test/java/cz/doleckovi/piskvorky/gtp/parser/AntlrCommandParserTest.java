package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.gtp.action.TestContext;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import cz.doleckovi.piskvorky.gtp.command.CommandAction;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests of {@link AntlrCommandParser} driving {@code parse(...)} end-to-end with
 * recording factories - each grammar area, id echo, ignored lines and syntax-error
 * handling gets its case.
 */
class AntlrCommandParserTest implements WithAssertions {

	private RecordingCommandFactory commandFactory;
	private RecordingActionFactory actionFactory;
	private AntlrCommandParser parser;

	@BeforeEach
	void setUp() {
		commandFactory = new RecordingCommandFactory();
		actionFactory = new RecordingActionFactory();
		parser = new AntlrCommandParser(commandFactory, actionFactory);
	}

	@Test
	void simpleCommand() {
		var action = parser.parse("version");
		assertThat(onlyCommandCall()).isEqualTo(new RecordingCommandFactory.Call("simple", "version", null));
		assertThat(action).isInstanceOf(CommandAction.class);
		assertThat(action.execute(TestContext.SILENT).format()).isEqualTo("= result\n");
	}

	@Test
	void numberCommand() {
		parser.parse("boardsize 19");
		assertThat(onlyCommandCall()).isEqualTo(new RecordingCommandFactory.Call("number", "boardsize", 19));
	}

	@Test
	void underscoresAndDashesInCommandName() {
		parser.parse("board_size 10");
		assertThat(onlyCommandCall().kind()).isEqualTo("number");
		assertThat(onlyCommandCall().name()).isEqualTo("board_size");
	}

	@Test
	void vertexCommand() {
		parser.parse("color H12");
		var call = onlyCommandCall();
		assertThat(call.kind()).isEqualTo("vertex");
		assertThat(call.name()).isEqualTo("color");
		assertThat(call.arg()).isInstanceOfSatisfying(Vertex.class, vertex -> {
			// 'I' is skipped in GTP columns, so H is the 8th letter -> index 7
			// Rows on Go board are numbered from 1, Board is zero based -> 12 -> 11
			assertThat(vertex.column()).isEqualTo(7);
			assertThat(vertex.row()).isEqualTo(11);
		});
	}

	@Test
	void sideCommand() {
		parser.parse("genmove black");
		assertThat(onlyCommandCall()).isEqualTo(new RecordingCommandFactory.Call("side", "genmove", Side.BLACK));
	}

	@Test
	void moveCommandWithFullMovesSequence() {
		parser.parse("play white A1 black B2 block C3");
		var call = onlyCommandCall();
		assertThat(call.kind()).isEqualTo("moves");
		assertThat(call.name()).isEqualTo("play");
		var moves = movesOf(call);
		assertThat(moves).hasSize(3);
		assertThat(moves.get(0).stone()).isEqualTo(Stone.WHITE);
		assertThat(moves.get(0).vertex()).hasToString("A1");
		assertThat(moves.get(1).stone()).isEqualTo(Stone.BLACK);
		assertThat(moves.get(1).vertex()).hasToString("B2");
		assertThat(moves.get(2).stone()).isEqualTo(Stone.BLOCK);
		assertThat(moves.get(2).vertex()).hasToString("C3");
	}

	@Test
	void moveCommandAcceptsAbbreviatedSidesAndLowerCaseVertices() {
		parser.parse("gogui-play_sequence white a3 b c7 W a2 Black d1");
		var moves = movesOf(onlyCommandCall());
		assertThat(moves).hasSize(4);
		assertThat(moves.get(0).stone()).isEqualTo(Stone.WHITE);
		assertThat(moves.get(0).vertex()).hasToString("A3");
		assertThat(moves.get(1).stone()).isEqualTo(Stone.BLACK); // "b"
		assertThat(moves.get(1).vertex()).hasToString("C7");
		assertThat(moves.get(2).stone()).isEqualTo(Stone.WHITE); // "W"
		assertThat(moves.get(3).stone()).isEqualTo(Stone.BLACK); // "Black"
	}

	@Test
	void kvpCommand() {
		parser.parse("piskvorky-config depth 3");
		assertThat(onlyCommandCall()).isEqualTo(new RecordingCommandFactory.Call("kvp", "piskvorky-config", "depth=3"));
	}

	@Test
	void textCommand() {
		parser.parse("known_command play");
		assertThat(onlyCommandCall()).isEqualTo(new RecordingCommandFactory.Call("text", "known_command", "play"));
	}

	@Test
	void textArgumentKeepsInternalSpaceRuns() {
		parser.parse("echo Hello    world!");
		assertThat(onlyCommandCall().arg()).isEqualTo("Hello    world!");
	}

	@Test
	void textArgumentKeepsTabs() {
		parser.parse("echo prefixed\t\tsuffix");
		assertThat(onlyCommandCall().arg()).isEqualTo("prefixed\t\tsuffix");
	}

	@Test
	void textArgumentTrimsLeadingWhitespace() {
		parser.parse("echo  leading");
		assertThat(onlyCommandCall().arg()).isEqualTo("leading");
	}

	@Test
	void textArgumentTrimsTrailingWhitespace() {
		parser.parse("echo trailing   ");
		assertThat(onlyCommandCall().arg()).isEqualTo("trailing");
	}

	@Test
	void textArgumentExcludesTrailingComment() {
		parser.parse("echo hi # note");
		assertThat(onlyCommandCall().arg()).isEqualTo("hi");
	}

	@Test
	void sentenceEchoWithApostropheAndSlash() {
		parser.parse("echo B12 can't be played w/o defence of 5th row black will put stone at offset 5");
		assertThat(onlyCommandCall().arg())
				.isEqualTo("B12 can't be played w/o defence of 5th row black will put stone at offset 5");
	}

	@Test
	void kvpValueIsRawSliced() {
		parser.parse("piskvorky-config white_name John F. Smith");
		assertThat(onlyCommandCall().arg()).isEqualTo("white_name=John F. Smith");
	}

	@Test
	void idIsExtractedAndPassedToCommandAction() {
		var action = parser.parse("42 boardsize 19");
		assertThat(action).isInstanceOfSatisfying(CommandAction.class,
				commandAction -> assertThat(commandAction.id()).isEqualTo("42"));
		assertThat(action.execute(TestContext.SILENT).format()).isEqualTo("= 42 result\n");
	}

	@Test
	void interruptLine() {
		var action = parser.parse("# interrupt");
		assertThat(onlyActionCall().kind()).isEqualTo("interrupt");
		assertThat(action.execute(TestContext.SILENT).format()).isNull();
	}

	@Test
	void emptyLineProducesNoOp() {
		assertThat(parser.parse("").execute(TestContext.SILENT).format()).isNull();
		assertThat(onlyActionCall().kind()).isEqualTo("noop");
	}

	@Test
	void whitespaceLineProducesNoOp() {
		assertThat(parser.parse("   \t").execute(TestContext.SILENT).format()).isNull();
		assertThat(onlyActionCall().kind()).isEqualTo("noop");
	}

	@Test
	void commentLineProducesNoOp() {
		assertThat(parser.parse("# just a comment").execute(TestContext.SILENT).format()).isNull();
		assertThat(onlyActionCall().kind()).isEqualTo("noop");
	}

	@Test
	void carriageReturnTolerated() {
		parser.parse("boardsize 19\r");
		assertThat(onlyCommandCall().kind()).isEqualTo("number");
	}

	@Test
	void trailingCommentAfterCommandIgnored() {
		parser.parse("version # trailing comment");
		assertThat(onlyCommandCall().kind()).isEqualTo("simple");
	}

	@Test
	void malformedLineWithoutIdBecomesSyntaxError() {
		var action = parser.parse("bogus !");
		var call = onlyActionCall();
		assertThat(call.kind()).isEqualTo("syntax");
		assertThat(call.id()).isNull();
		assertThat(action.execute(TestContext.SILENT).format()).startsWith("? ").endsWith("\n");
	}

	@Test
	void malformedLineWithIdEchoesId() {
		var action = parser.parse("42 bogus !");
		var call = onlyActionCall();
		assertThat(call.kind()).isEqualTo("syntax");
		assertThat(call.id()).isEqualTo("42");
		assertThat(action.execute(TestContext.SILENT).format()).startsWith("? 42 ");
	}

	@Test
	void loneIdIsSyntaxError() {
		var action = parser.parse("42");
		assertThat(onlyActionCall().kind()).isEqualTo("syntax");
		assertThat(action.execute(TestContext.SILENT).format()).startsWith("? 42 ");
	}

	@Test
	void textCommandWithoutArgumentFallsBackToSimple() {
		// ANTLR prediction prefers the viable COMMAND (simple) alternative when no WORD
		// follows, so "echo" alone is a well-formed no-argument command
		parser.parse("echo ");
		assertThat(onlyCommandCall().kind()).isEqualTo("simple");
		assertThat(onlyCommandCall().name()).isEqualTo("echo");
	}

	@Test
	void illegalVertexColumnIsSyntaxError() {
		// 'I' is excluded from the VERTEX lexer token, so it fails at token recognition
		// (the I-skipping inside Vertex.parse is unreachable through the grammar)
		var action = parser.parse("dig I5");
		assertThat(onlyActionCall().kind()).isEqualTo("syntax");
		assertThat(action.execute(TestContext.SILENT).format()).startsWith("? ");
	}

	@Test
	void outOfRangeVertexRowIsSyntaxError() {
		var action = parser.parse("dig A27");
		assertThat(onlyActionCall().kind()).isEqualTo("syntax");
		assertThat(action.execute(TestContext.SILENT).format()).contains("Invalid vertex: A27");
	}

	@Test
	void numberOverflowIsSyntaxError() {
		var action = parser.parse("boardsize 99999999999999999999");
		assertThat(onlyActionCall().kind()).isEqualTo("syntax");
		assertThat(action.execute(TestContext.SILENT).format()).startsWith("? ");
	}

	@Test
	void multiLineInputIsRejectedNotSilentlyTruncated() {
		var action = parser.parse("version\nversion");
		assertThat(onlyActionCall().kind()).isEqualTo("syntax");
		assertThat(action.execute(TestContext.SILENT).format()).contains("trailing content");
	}

	@Test
	void interruptWithGarbageIsSilentlyIgnoredAsComment() {
		// Lexer longest match: COMMENT ("#" + rest of line, 17 chars) beats INTERRUPT
		// ("# interrupt", 11 chars), so the whole line is a comment -> noop. Plain
		// "# interrupt" only matches INTERRUPT thanks to the same-length tie resolved by
		// declaration order - fragile, tracked by TODO [15].
		var action = parser.parse("# interrupt extra");
		assertThat(onlyActionCall().kind()).isEqualTo("noop");
		assertThat(action.execute(TestContext.SILENT).format()).isNull();
	}

	@Test
	void nullLineRejected() {
		assertThatNullPointerException().isThrownBy(() -> parser.parse(null));
	}

	@Test
	void errorPositionsAreNumberedAcrossTheStream() {
		parser.parse("version");
		parser.parse("");
		assertThat(parser.parse("bogus !").execute(TestContext.SILENT).format())
				.as("lexer error on stream line 3").contains("at 3:");
		parser.parse("version");
		assertThat(parser.parse("42").execute(TestContext.SILENT).format())
				.as("parser error on stream line 5").contains("at 5:");
	}

	private RecordingCommandFactory.Call onlyCommandCall() {
		assertThat(commandFactory.calls).as("exactly one command created").hasSize(1);
		return commandFactory.calls.getFirst();
	}

	private RecordingActionFactory.Call onlyActionCall() {
		assertThat(actionFactory.calls).as("exactly one action created").hasSize(1);
		return actionFactory.calls.getFirst();
	}

	@SuppressWarnings("unchecked")
	private static List<Move> movesOf(RecordingCommandFactory.Call call) {
		return (List<Move>) call.arg();
	}

}
