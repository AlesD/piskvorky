package cz.doleckovi.piskvorky.core;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Position;
import cz.doleckovi.piskvorky.core.position.PositionImpl;

public class GameImpl implements Game {

    private Position position;
    private Side sideToMove;

    public GameImpl(Position position) {
        this.position = position;
        this.sideToMove = Side.BLACK;
    }

    public GameImpl(Board board) {
        this(new PositionImpl(board));
    }

    public Position position() {
        return position;
    }

    @Override
    public Side sideToMove() {
        return sideToMove;
    }

    @Override
    public void move(Side side, int column, int row) {
        var field = position.board().field(column, row);
        position = position.withStone(field, side.stone);
        if (sideToMove == side) sideToMove = switch (side) {
            case WHITE -> Side.BLACK;
            case BLACK -> Side.WHITE;
        };
    }

}
