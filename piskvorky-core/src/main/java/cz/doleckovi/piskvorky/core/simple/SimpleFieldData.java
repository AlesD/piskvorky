package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.position.FieldData;
import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.List;

public class SimpleFieldData implements FieldData {

	public static final FieldData EMPTY = new SimpleFieldData(Stone.EMPTY, 0);

	private final Stone stone;
	private int[] white;
	private int[] black;
	private final int move;

	private SimpleFieldData(Stone stone, int[] white, int[] black, int move) {
		this.stone = stone;
		this.white = white;
		this.black = black;
		this.move = move;
	}

	@Override
	public Stone stone() {
		return stone;
	}

	int move() {
		return move;
	}

	SimpleFieldData withCell(SimpleCellData oldData, SimpleCellData newData) {
		var white = this.white.clone();
		var black = this.black.clone();
		--white[oldData.white()];
		--black[oldData.black()];
		++white[newData.white()];
		++black[newData.black()];
		return new SimpleFieldData(stone, white, black, move - oldData.move() + newData.move());
	}

	Updater updater(Stone stone) {
		return new Updater(stone, white.clone(), black.clone(), move);
	}

	static class Updater {

		private final Stone stone;
		private final int[] white;
		private final int[] black;
		private int move;

		public Updater(Stone stone, int[] white, int[] black, int move) {
			this.stone = stone;
			this.white = white;
			this.black = black;
			this.move = move;
		}

		SimpleCellData withCell(SimpleCellData oldData, SimpleCellData newData) {
			--white[oldData.white()];
			--black[oldData.black()];
			++white[newData.white()];
			++black[newData.black()];
			move = move - oldData.move() + newData.move();
			return newData;
		}

		SimpleFieldData newData() {
			return new SimpleFieldData(stone, white, black, move);
		}

	}

}
