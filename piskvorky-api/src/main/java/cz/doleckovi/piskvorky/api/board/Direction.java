package cz.doleckovi.piskvorky.api.board;

/** Direction on the board. */
public enum Direction {

	/** Increases column number. */
	HORIZONTAL(0, 1),
	/** Increases column number and line number. */
	DOWNHILL(1, 1),
	/** Increases line number. */
	VERTICAL(1, 0),
	/** Increases line number and decreases column number. */
	UPHILL(1, -1);

	public final int rowDelta;
	public final int columnDelta;

	Direction(int rowDelta, int columnDelta) {
		this.rowDelta = rowDelta;
		this.columnDelta = columnDelta;
	}

}
