package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.api.board.Board;

public interface Session {

	Board getBoard();
	void setBoard(int size);

	Game getGame();
	void resetGame();

}
