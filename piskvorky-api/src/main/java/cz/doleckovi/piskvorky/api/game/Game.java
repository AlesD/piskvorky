package cz.doleckovi.piskvorky.api.game;

import cz.doleckovi.piskvorky.api.position.Position;

/** Game. */
public interface Game<P extends Position> {

	/** Game ID. */
	int getId();

	/** Current game position. */
	P getPosition();

}
