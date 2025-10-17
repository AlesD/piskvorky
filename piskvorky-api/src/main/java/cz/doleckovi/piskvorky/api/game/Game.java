package cz.doleckovi.piskvorky.api.game;

import cz.doleckovi.piskvorky.api.board.Position;

/** Game. */
public interface Game<P extends Position<P>> {

	/** Game ID. */
	int getId();

	/** Current game position. */
	P getPosition();

}
