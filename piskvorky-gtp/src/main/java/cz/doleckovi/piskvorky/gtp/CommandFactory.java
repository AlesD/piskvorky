package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.InterruptCommand;
import cz.doleckovi.piskvorky.gtp.command.NoOpCommand;

import java.util.List;

public interface CommandFactory {

	Command createSimpleCommand(String commandName);
	Command createSideCommand(String commandName, Side side);
	Command createTextCommand(String commandName, String text);
	Command createNumberCommand(String commandName, Integer integer);
	Command createVertexCommand(String commandName, Vertex vertex);
	Command createMoveCommand(String commandName, List<Move> moves);
	Command createKVPCommand(String commandName, String key, String value);

	default Command createInterruptAction() {
		return InterruptCommand.INSTANCE;
	}

	default Command createNoOpAction() {
		return NoOpCommand.INSTANCE;
	}

}
