package cz.doleckovi.piskvorky.api.game;

import cz.doleckovi.piskvorky.api.board.Player;

/** Move playable in specific position. */
public interface Move {

	/** Column where stone is put.
	 * @return Column number
	 */
	int getColumn();

	/** Gets row where stone is put.
	 * @return Row number
	 */
	int getRow();

	/** Gets player who made the move.
	 * @return Player instance
	 */
	Player getPlayer();

}
