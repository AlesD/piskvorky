package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.position.Cell;
import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.Arrays;

public class SimpleCell implements Cell {

	public static final SimpleCell EMPTY = new SimpleCell(Stone.EMPTY, 0, 0);

	static SimpleCell[] arrayOf(int length) {
		var result = new SimpleCell[length];
		Arrays.fill(result, EMPTY);
		return result;
	}

	private final Stone stone;
	private final int white;
	private final int black;

	public SimpleCell(Stone stone, int white, int black) {
		this.stone = stone;
		this.white = white;
		this.black = black;
	}

	@Override
	public Stone stone() {
		return stone;
	}

	int white() {
		return white;
	}

	int black() {
		return black;
	}

	/** Converts empty cell to cell with stone.
	 * @param stone Stone to place in cell
	 * @return Cell with stone
	 * @throws IllegalStateException if {@code this.stone()} is not {@link Stone#EMPTY empty}
	 * @throws IllegalArgumentException if the stone is {@link Stone#EMPTY empty}
	 */
	SimpleCell withStone(Stone stone) {
		if (this.stone != Stone.EMPTY) throw new IllegalStateException("Cell is not empty");
		return switch (stone) {
			case EMPTY -> throw new IllegalArgumentException("Stone is EMPTY");
			case WHITE -> new SimpleCell(stone, white + 1, 0);
			case BLACK -> new SimpleCell(stone, 0, black + 1);
			case BLOCK -> new SimpleCell(stone, 0, 0);
		};
	}

}
