package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.position.Side;

/** Move playable in specific position. */
public interface Move extends FieldAddress {

	/** Gets side making the move.
	 * @return Side making the move
	 */
	Side side();

}
