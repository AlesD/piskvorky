package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.api.board.Side;

import java.util.List;

public interface CommandFactory {

    GTPCommand createSimpleCommand(String commandName);
    GTPCommand createColorCommand(String commandName, Side side);
    GTPCommand createTextCommand(String commandName, String text);
    GTPCommand createNumberCommand(String commandName, String integer);
    GTPCommand createMoveCommand(String commandName, Move move);
    GTPCommand createVertexCommand(String commandName, Vertex vertex);
    GTPCommand createMovesCommand(String commandName, List<Move> moves);
    GTPCommand createKVPCommand(String commandName, String key, String value);

}
