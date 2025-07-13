package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.search.Player;
import cz.doleckovi.piskvorky.api.search.Score;

class ScoreImpl implements Score<ScoreImpl> {

	public static final ScoreImpl ZERO;

	static {
		var empty = new int[StoneClass.values().length];
		ZERO = new ScoreImpl(false, empty, empty);
	}

	private final int[] white;
	private final int[] black;
	private final int[] diff;
	private final int[] sum;
	final int cardinality;
	private final boolean terminal;
	private final boolean draw;

	ScoreImpl(boolean draw, int[] white, int[] black) {
		assert white.length == black.length;
		this.white = white;
		this.black = black;
		diff = new int[white.length];
		sum = new int[white.length];
		int cardinality = 0;
		int diff = 0;
		int sum = 0;
		for (int index = 0; index < white.length; ++index) {
			diff = diff + white[index] - black[index];
			this.diff[index] = diff;
			if (diff != 0)
				cardinality = index;
			sum = sum + white[index] + black[index];
			this.sum[index] = sum;
		}
		this.cardinality = cardinality;
		this.terminal = cardinality == white.length - 1;
		this.draw = draw;
	}

	ScoreImpl(int[] white, int[] black) {
		this(false, white, black);
	}

	public ScoreImpl self() {
		return this;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public boolean isDraw() {
		return draw;
	}

	@Override
	public boolean isBetterThan(ScoreImpl other, Player player) {
		assert white.length == other.white.length;
		int index = Integer.max(cardinality, other.cardinality);
		do {
			int result = white[index] - black[index] - other.white[index] + other.black[index];
			if (result != 0) return switch (player) {
				case WHITE -> result > 0;
				case BLACK -> result < 0;
			};
		} while (--index >= 0);
		return false;
	}

	@Override
	public ScoreImpl betterOf(ScoreImpl other, Player player) {
		return other == null || isBetterThan(other, player) ? self() : other;
	}

}
