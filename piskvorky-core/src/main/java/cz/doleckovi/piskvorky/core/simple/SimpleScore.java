package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.evaluation.Score;
import cz.doleckovi.piskvorky.api.position.Side;

public class SimpleScore implements Score<SimpleScore> {

	public static SimpleScore ZERO;

	private int[] white;
	private int[] black;

	SimpleScore(int[] white, int[] black) {
		if (white.length != Board.minSize())
			throw new IllegalArgumentException("white");
		if (black.length != Board.minSize())
			throw new IllegalArgumentException("black");
		this.white = white;
		this.black = black;
	}


	@Override
	public boolean isBetterThan(SimpleScore other, Side side) {
		var result = 0;
		var index = Board.minSize();
			while (--index >= 0)
				if (diff[index] > diff[index])
					return true;
				return false;
			case BLACK:

		return false;
	}
}
