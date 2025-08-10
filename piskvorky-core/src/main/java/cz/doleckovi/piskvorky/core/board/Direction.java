package cz.doleckovi.piskvorky.core.board;

/** Direction on the board. */
public enum Direction {

	// The names are chosen to be self-descriptive as if you "read from left to right".
	// The order is chosen so that if the board is represented as a one-dimensional array, then index delta is
	// increasing if dimension is increasing.

	/** Increases column number. */
	HORIZONTAL(0, 1),
	/** Increases line number and decreases column number. */
	UPHILL(1, -1),
	/** Increases line number. */
	VERTICAL(1, 0),
	/** Increases column number and line number. */
	DOWNHILL(1, 1);

	public final int rowDelta;
	public final int columnDelta;

	Direction(int rowDelta, int columnDelta) {
		this.rowDelta = rowDelta;
		this.columnDelta = columnDelta;
	}

	@Override
	public String toString() {
		return switch (this) {
			case HORIZONTAL -> "⭢"; // U+2B62 RIGHTWARDS TRIANGLE-HEADED ARROW
			case UPHILL     -> "⭩"; // U+2B67 SOUTH WEST TRIANGLE-HEADED ARROW
			case VERTICAL   -> "⭣"; // U+2B63 DOWNWARDS TRIANGLE-HEADED ARROW
			case DOWNHILL   -> "⭨"; // U+2B68 SOUTH EAST TRIANGLE-HEADED ARROW
		};
	}

}
