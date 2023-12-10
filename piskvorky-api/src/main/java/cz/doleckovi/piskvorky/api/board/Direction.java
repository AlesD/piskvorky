package cz.doleckovi.piskvorky.api.board;

/** Direction on the board.
 * <p>All directions increase field indexes.</p>
 */
public enum Direction {
	/** Increases column number. */
	LEFT,
	/** Increases column number and row number. */
	LEFT_DOWN,
	/** Increases row number. */
	DOWN,
	/** Increases row number and decreases column number. */
	DOWN_RIGHT
}
