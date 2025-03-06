package cz.doleckovi.piskvorky.api.move;

import cz.doleckovi.piskvorky.api.board.Player;

/** Move playable in specific position. */
public interface Move {

	/** Gets player making the move. */
	Player getPlayer();

	/** Gets column where stone is put. */
	int getColumn();

	/** Gets line where stone is put. */
	int getRow();

}
