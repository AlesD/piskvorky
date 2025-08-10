package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Stone;

public record Field(Stone stone) {

	public static final Field EMPTY = new Field(Stone.EMPTY);

	Field withStone(Stone stone) {
		assert this.stone == Stone.EMPTY : "Field must be empty";
		assert stone != Stone.EMPTY : "Stone can't be empty";
		return new Field(stone);
	}

	@Override
	public String toString() {
		return stone.toString();
	}
}
