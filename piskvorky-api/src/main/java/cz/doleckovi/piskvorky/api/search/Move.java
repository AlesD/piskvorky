package cz.doleckovi.piskvorky.api.search;

/** Move playable in specific position. */
public interface Move {

	/** Gets player making the move. */
	Player player();

	/** Gets column where player stone is put. */
	int column();

	/** Gets line where player stone is put. */
	int row();

}
