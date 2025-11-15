package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.gtp.command.Action;
import cz.doleckovi.piskvorky.gtp.command.CommandException;
import cz.doleckovi.piskvorky.gtp.command.InterruptAction;
import cz.doleckovi.piskvorky.gtp.command.NoOpAction;

import java.util.List;

public interface CommandFactory {

    Object createSimpleCommand(String commandName);
    Object createSideCommand(String commandName, Side side);
    Object createTextCommand(String commandName, String text);
    Object createNumberCommand(String commandName, String integer);
    Object createMoveCommand(String commandName, Move move);
    Object createVertexCommand(String commandName, Vertex vertex);
    Object createMovesCommand(String commandName, List<Move> moves);
    Object createKVPCommand(String commandName, String key, String value);

	default Object createInterruptAction() {
		return InterruptAction.INSTANCE;
	}

	default Object createNoOpAction() {
		return NoOpAction.INSTANCE;
	}

}
