package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.api.board.Field;

public class FieldImpl implements Field {

	final int generation;
	final Stone stone;

	FieldImpl(Stone stone, int generation) {
		this.stone = stone;
		this.generation = generation;
	}

	@Override
	public Stone getStone() {
		return stone;
	}

}
