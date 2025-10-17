package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Side;

/** Move playable in specific position. */
public interface Move {

	/** Gets side making the move.
	 * @return Side making the move
	 */
	Side side();

	/** Gets column where player stone is put. */
	int column();

	/** Gets line where player stone is put. */
	int row();

}
