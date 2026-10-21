package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command assembler for the standard GTP command set (spec §6): administrative,
 * setup and play commands. It holds no state - commands reach the session's board,
 * game and messages through the {@link cz.doleckovi.piskvorky.gtp.action.Context}
 * of their execution (TODO.md [23] follow-up), so the factory is shareable across
 * sessions of identical wiring. Names it does not know produce {@link UnknownCommand}
 * bodies; GoGui commands live in
 * {@link cz.doleckovi.piskvorky.gtp.command.gogui.GoGuiCommandFactory} and its
 * {@link cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesCommandFactory rules
 * package}, and a {@link CompositeCommandFactory} presents all known commands as
 * one factory.
 */
public class GtpCommandFactory implements CommandFactory {

	private static final Logger LOG = Logger.getLogger(GtpCommandFactory.class.getName());

	public static final List<String> KNOWN_COMMANDS = List.of(
			// Standard administrative commands
			ProtocolVersionCommand.NAME,
			NameCommand.NAME,
			VersionCommand.NAME,
			KnownCommandCommand.NAME,
			ListCommandsCommand.NAME,
			QuitCommand.NAME,
			// Standard setup commands
			BoardsizeCommand.NAME,
			ClearBoardCommand.NAME,
			// Core play commands
			PlayCommand.NAME,
			UndoCommand.NAME
	);

	private static final String VERSION;

	static {
		Properties properties = new Properties();
		var stream = GtpCommandFactory.class.getClassLoader().getResourceAsStream("META-INF/maven/cz.doleckovi.piskvorky/piskvorky-gtp/pom.properties");
		if (stream != null) try {
			properties.load(stream);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to load pom.properties", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				LOG.log(Level.SEVERE, "Failed to close pom.properties", e);
			}
		}
		VERSION = properties.getProperty("version", "unknown");
	}

	@Override
	public Collection<String> supportedCommands() {
		return KNOWN_COMMANDS;
	}

	@Override
	public Command createSimpleCommand(String commandName) {
		return switch (commandName) {
			case ProtocolVersionCommand.NAME -> createProtocolVersionCommand();
			case NameCommand.NAME -> createNameCommand();
			case VersionCommand.NAME -> createVersionCommand();
			case ListCommandsCommand.NAME -> createListCommandsCommand();
			case QuitCommand.NAME -> createQuitCommand();
			case ClearBoardCommand.NAME -> createClearBoardCommand();
			case UndoCommand.NAME -> createUndoCommand();
			default -> new UnknownCommand(commandName);
		};
	}

	@Override
	public Command createNumberCommand(String commandName, int arg) {
		return switch (commandName) {
			case BoardsizeCommand.NAME -> createBoardsizeCommand(arg);
			default -> new UnknownCommand(commandName, arg);
		};
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
		return switch (commandName) {
			case PlayCommand.NAME -> createPlayCommand(moves);
			default -> new UnknownCommand(commandName, moves.toArray());
		};
	}

	@Override
	public Command createKVPCommand(String commandName, String key, String value) {
		return new UnknownCommand(commandName, key, value);
	}

	@Override
	public Command createTextCommand(String commandName, String text) {
		return switch (commandName) {
			case KnownCommandCommand.NAME -> createKnownCommandCommand(text);
			default -> new UnknownCommand(commandName, text);
		};
	}

	public Command createProtocolVersionCommand() {
		return ProtocolVersionCommand.INSTANCE;
	}

	public Command createNameCommand() {
		return new NameCommand("Piskvorky");
	}

	public Command createVersionCommand() {
		return new VersionCommand(VERSION);
	}

	public Command createKnownCommandCommand(String commandName) {
		return new KnownCommandCommand(commandName, KNOWN_COMMANDS.contains(commandName));
	}

	public Command createListCommandsCommand() {
		return new ListCommandsCommand(KNOWN_COMMANDS);
	}

	public Command createQuitCommand() {
		return new QuitCommand();
	}

	public Command createBoardsizeCommand(int size) {
		return new BoardsizeCommand(size);
	}

	public Command createClearBoardCommand() {
		return new ClearBoardCommand();
	}

	public Command createPlayCommand(List<Move> moves) {
		if (moves.size() != 1) throw new IllegalArgumentException("Exactly 1 move expected");
		var move = moves.getFirst();
		var side = switch (move.stone()) {
			case WHITE -> Side.WHITE;
			case BLACK -> Side.BLACK;
			default -> throw new IllegalArgumentException("Stone must be only white or black");
		};
		var vertex = move.vertex();
		return new PlayCommand(side, vertex.column(), vertex.row());
	}

	public Command createUndoCommand() {
		return new UndoCommand();
	}

}
