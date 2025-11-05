package cz.doleckovi.piskvorky.api;

import cz.doleckovi.piskvorky.api.board.Direction;

public interface Constants {

	/** Number of consecutive stones of one side that win the game. */
	int WIN_LENGTH = 5;
    /** Number of directions.
     * <p>Same as {@code Direction.values().length}.</p>
     */
    int DIRECTION_COUNT = Direction.values().length;

}
