package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.traits.IdTrait;

/** Move playable in specific position. */
public interface Move extends IdTrait<Integer> {

	/** Gets player making the move. */
	Player getPlayer();

	/** Gets column where stone is put. */
	int getColumn();

	/** Gets row where stone is put. */
	int getRow();

}
