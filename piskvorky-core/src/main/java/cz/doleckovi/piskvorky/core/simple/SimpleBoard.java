package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Board;

public class SimpleBoard implements Constants, Board<SimplePosition> {

	private final int width;
	private final int height;

	public SimpleBoard(int size) throws IllegalArgumentException {
		if (size < SIZE)
			throw new IllegalArgumentException("Size of the board must be at least " + SIZE);
		width = size;
		height = size;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public SimplePosition initialPosition() {
		return new SimplePosition(this);
	}

}
