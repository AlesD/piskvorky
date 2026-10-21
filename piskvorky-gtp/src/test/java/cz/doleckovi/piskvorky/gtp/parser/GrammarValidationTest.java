package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.gtp.command.CompositeCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.GtpCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.BoardsizeCommand;
import cz.doleckovi.piskvorky.gtp.command.ClearBoardCommand;
import cz.doleckovi.piskvorky.gtp.command.KnownCommandCommand;
import cz.doleckovi.piskvorky.gtp.command.ListCommandsCommand;
import cz.doleckovi.piskvorky.gtp.command.NameCommand;
import cz.doleckovi.piskvorky.gtp.command.PlayCommand;
import cz.doleckovi.piskvorky.gtp.command.ProtocolVersionCommand;
import cz.doleckovi.piskvorky.gtp.command.QuitCommand;
import cz.doleckovi.piskvorky.gtp.command.UndoCommand;
import cz.doleckovi.piskvorky.gtp.command.VersionCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.GoGuiCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.gogui.InterruptCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.SigIntCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesBoardCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesBoardGfxCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesBoardSizeCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesCapturedCountCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesFinalResultCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesGameIdCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesLegalMovesCommand;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesSideToMoveCommand;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

/**
 * Validates the grammar over every command known to the composed
 * {@link GtpCommandFactory}, {@link GoGuiCommandFactory} and
 * {@link RulesCommandFactory}: each must parse into its expected shape without
 * a syntax error.
 */
class GrammarValidationTest implements WithAssertions {

	private static CompositeCommandFactory allCommandsFactory() {
		return new CompositeCommandFactory(new GtpCommandFactory(), new GoGuiCommandFactory(), new RulesCommandFactory());
	}

	private record CommandWithArguments(String commandName, String kind, String arguments) {
		@Override
		public String toString() {
			return arguments == null ? commandName : commandName + ' ' + arguments;
		}
	}

	static final List<CommandWithArguments> ALL_COMMANDS = List.of(
			// Standard administrative commands
			new CommandWithArguments(ProtocolVersionCommand.NAME, "simple", null),
			new CommandWithArguments(NameCommand.NAME, "simple", null),
			new CommandWithArguments(VersionCommand.NAME, "simple", null),
			new CommandWithArguments(KnownCommandCommand.NAME, "text", KnownCommandCommand.NAME),
			new CommandWithArguments(ListCommandsCommand.NAME, "simple", null),
			new CommandWithArguments(QuitCommand.NAME, "simple", null),
			// Standard setup commands
			new CommandWithArguments(BoardsizeCommand.NAME, "number", "19"),
			new CommandWithArguments(ClearBoardCommand.NAME, "simple", null),
			// Core play commands
			new CommandWithArguments(PlayCommand.NAME, "moves", "white A1"),
			new CommandWithArguments(UndoCommand.NAME, "simple", null),
			// Go GUI rules extension commands
			new CommandWithArguments(RulesGameIdCommand.NAME, "simple", null),
			new CommandWithArguments(RulesBoardCommand.NAME, "simple", null),
			new CommandWithArguments(RulesBoardGfxCommand.NAME, "simple", null),
			new CommandWithArguments(RulesCapturedCountCommand.NAME, "simple", null),
			new CommandWithArguments(RulesBoardSizeCommand.NAME, "simple", null),
			new CommandWithArguments(RulesLegalMovesCommand.NAME, "simple", null),
			new CommandWithArguments(RulesSideToMoveCommand.NAME, "simple", null),
			new CommandWithArguments(RulesFinalResultCommand.NAME, "simple", null),
			// Go GUI interrupt
			new CommandWithArguments(InterruptCommand.NAME, "simple", null),
			new CommandWithArguments(SigIntCommand.NAME, "simple", null)
	);

	@Test
	void allCommandsTested() {
		var knownCommands = new HashSet<>(allCommandsFactory().supportedCommands());
		ALL_COMMANDS.forEach(command -> knownCommands.remove(command.commandName()));
		assertThat(knownCommands).as("All known commands are sent to parser").isEmpty();
	}

	@Test
	void everyKnownCommandParsesToExpectedShape() {
		for (var command : ALL_COMMANDS) {
			var commandFactory = new RecordingCommandFactory();
			var parser = new AntlrCommandParser(commandFactory, new RecordingActionFactory());
			parser.parse(command.toString());
			assertThat(commandFactory.calls)
					.as("Command %s parsed", command)
					.singleElement()
					.satisfies(call -> {
						assertThat(call.kind()).isEqualTo(command.kind());
						assertThat(call.name()).isEqualTo(command.commandName());
					});
		}
	}

	@Test
	void everyKnownCommandReachesRealFactory() {
		for (var command : ALL_COMMANDS) {
			var parser = new AntlrCommandParser(allCommandsFactory(), new RecordingActionFactory());
			var action = parser.parse(command.toString());
			assertThat(action.toString())
					.as("Command %s created and wrapped without error", command)
					.startsWith("CommandAction[");
		}
	}

}
