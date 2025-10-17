package cz.doleckovi.piskvorky.core.simple;

enum Direction {

	HORIZONTAL(1, 0),
	VERTICAL(0, 1),
	DOWNHILL(1, 1),
	UPHILL(1, -1);

	public int columnDelta;
	public int rowDelta;

	Direction(int columnDelta, int rowDelta) {
		this.columnDelta = columnDelta;
		this.rowDelta = rowDelta;
	}

}
