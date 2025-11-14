package cz.doleckovi.piskvorky.gtp.session;

import cz.doleckovi.piskvorky.api.Game;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.gtp.Session;

import java.util.function.Function;
import java.util.function.IntFunction;

public class SessionImpl implements Session {

    private final IntFunction<? extends Board> boardFactory;
    private final Function<? super Board, ? extends Game> gameFactory;

    private Board board;
    private Game game;
    private boolean alive = true;

    public SessionImpl(IntFunction<? extends Board> boardFactory, Function<? super Board, ? extends Game> gameFactory) {
        this.boardFactory = boardFactory;
        this.gameFactory = gameFactory;
        setBoard(19);
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public void setBoard(int size) {
        var newBoard = boardFactory.apply(size);
        var newGame = gameFactory.apply(newBoard);
        this.board = newBoard;
        this.game = newGame;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void resetGame() {
        game = gameFactory.apply(board);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

}
