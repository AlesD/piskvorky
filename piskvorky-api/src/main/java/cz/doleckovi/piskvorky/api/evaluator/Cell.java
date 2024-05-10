package cz.doleckovi.piskvorky.api.evaluator;

import cz.doleckovi.piskvorky.api.board.Player;
import cz.doleckovi.piskvorky.api.board.Stone;

/** Cell of line.
 */
public interface Cell {
	Stone getStone();
	int getValue(Player player);
}
