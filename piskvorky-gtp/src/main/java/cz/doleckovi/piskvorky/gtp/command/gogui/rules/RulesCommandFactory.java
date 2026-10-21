package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandFactory;
import cz.doleckovi.piskvorky.gtp.command.UnknownCommand;

import java.util.Collection;
import java.util.List;

/** Command factory for the {@code gogui-rules_*} commands. */
public class RulesCommandFactory implements CommandFactory {

	public static final List<String> KNOWN_COMMANDS = List.of(
			RulesGameIdCommand.NAME,
			RulesBoardCommand.NAME,
			RulesBoardGfxCommand.NAME,
			RulesCapturedCountCommand.NAME,
			RulesBoardSizeCommand.NAME,
			RulesLegalMovesCommand.NAME,
			RulesSideToMoveCommand.NAME,
			RulesFinalResultCommand.NAME
	);

	@Override
	public Collection<String> supportedCommands() {
		return KNOWN_COMMANDS;
	}

	@Override
	public Command createSimpleCommand(String commandName) {
		return switch (commandName) {
			case RulesGameIdCommand.NAME -> createRulesGameIdCommand();
			case RulesBoardCommand.NAME -> createRulesBoardCommand();
			case RulesBoardGfxCommand.NAME -> createRulesBoardGfxCommand();
			case RulesCapturedCountCommand.NAME -> createRulesCapturedCountCommand();
			case RulesBoardSizeCommand.NAME -> createRulesBoardSizeCommand();
			case RulesLegalMovesCommand.NAME -> createRulesLegalMovesCommand();
			case RulesSideToMoveCommand.NAME -> createRulesSideToMoveCommand();
			case RulesFinalResultCommand.NAME -> createRulesFinalResultCommand();
			default -> new UnknownCommand(commandName);
		};
	}

	@Override
	public Command createNumberCommand(String commandName, int arg) {
		return new UnknownCommand(commandName, arg);
	}

	@Override
	public Command createSideCommand(String commandName, Side side) {
		return new UnknownCommand(commandName, side);
	}

	@Override
	public Command createVertexCommand(String commandName, Vertex vertex) {
		return new UnknownCommand(commandName, vertex);
	}

	@Override
	public Command createMoveCommand(String commandName, List<Move> moves) {
		return new UnknownCommand(commandName, moves.toArray());
	}

	@Override
	public Command createKVPCommand(String commandName, String key, String value) {
		return new UnknownCommand(commandName, key, value);
	}

	@Override
	public Command createTextCommand(String commandName, String text) {
		return new UnknownCommand(commandName, text);
	}

	public Command createRulesGameIdCommand() {
		return new RulesGameIdCommand();
	}

	public Command createRulesBoardCommand() {
		return new RulesBoardCommand();
	}

	public Command createRulesBoardGfxCommand() {
		return new RulesBoardGfxCommand();
	}

	public Command createRulesCapturedCountCommand() {
		return new RulesCapturedCountCommand();
	}

	public Command createRulesBoardSizeCommand() {
		return new RulesBoardSizeCommand();
	}

	public Command createRulesLegalMovesCommand() {
		return new RulesLegalMovesCommand();
	}

	public Command createRulesSideToMoveCommand() {
		return new RulesSideToMoveCommand();
	}

	public Command createRulesFinalResultCommand() {
		return new RulesFinalResultCommand();
	}

}
