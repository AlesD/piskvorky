package cz.doleckovi.piskvorky.gtp.command.gogui;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandFactory;
import cz.doleckovi.piskvorky.gtp.command.UnknownCommand;

import java.util.Collection;
import java.util.List;

/** Command factory for the {@code gogui_*} commands. */
public class GoGuiCommandFactory implements CommandFactory {

	public static final List<String> KNOWN_COMMANDS = List.of(
			// Go GUI interrupt
			InterruptCommand.NAME,
			SigIntCommand.NAME
	);

	@Override
	public Collection<String> supportedCommands() {
		return KNOWN_COMMANDS;
	}

	@Override
	public Command createSimpleCommand(String commandName) {
		return switch (commandName) {
			case InterruptCommand.NAME -> createInterruptCommand();
			case SigIntCommand.NAME -> createSigIntCommand();
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

	public Command createInterruptCommand() {
		return new InterruptCommand();
	}

	public Command createSigIntCommand() {
		return new SigIntCommand();
	}

}
