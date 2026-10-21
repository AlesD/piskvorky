package cz.doleckovi.piskvorky.api.game;

import cz.doleckovi.piskvorky.api.position.Position;

public interface Game {
	Position position();
	Side sideToMove();
	void move(Side side, int column, int row);
}
