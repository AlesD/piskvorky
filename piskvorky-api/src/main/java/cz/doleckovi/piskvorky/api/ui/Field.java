package cz.doleckovi.piskvorky.api.ui;

import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.api.traits.IdTrait;

/** Field of the board for specific position. */
public interface Field extends IdTrait {
	/** Board column. */
	int getColumn();
	/** Board line. */
	int getRow();
	/** Stone on this fileld in specific position. */
	Stone getStone();
}
