package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.search.Player;
import cz.doleckovi.piskvorky.api.search.Score;

import java.util.StringJoiner;

/** Score based on the count of stones belonging to each of the stone classes that both players have. */
class StoneClassScore implements Score<StoneClassScore> {

	public static final StoneClassScore ZERO;

	static {
		var empty = new int[StoneClass.values().length];
		ZERO = new StoneClassScore(empty, empty);
	}

	private final int[] diff;
	private final int[] sum;
	private final boolean terminal;

	StoneClassScore(int[] white, int[] black) {
		assert white.length == black.length;
		diff = new int[white.length];
		sum = new int[white.length];
		int cardinality = 0;
		int index = white.length;
		while (--index >= 0) {
			diff[index] = white[index] - black[index];
			if ((sum[index] = white[index] + black[index]) != 0 && cardinality == 0)
				cardinality = index + 1;
		}
		this.terminal = sum[sum.length - 1] != 0;
	}

	public StoneClassScore self() {
		return this;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public boolean isBetterThan(StoneClassScore other, Player player) {
		assert diff.length == other.diff.length;
		if (terminal ^ other.terminal) return terminal;
		int firstNonZeroDiff = 0;
		// Highest inequal difference decides
		int index = diff.length;
		while (--index != 0) {
			int result = diff[index] - other.diff[index];
			if (result != 0) return switch (player) {
				case WHITE -> result > 0;
				case BLACK -> result < 0;
			};
			// Note:
			// * diff[index] is equal to other.diff[index] here => it does not matter which one we use
			// * There is no need to test that the difference is non-zero - for zero the firstNonZeroDiff remains zero
			if (firstNonZeroDiff == 0) firstNonZeroDiff = diff[index];
		}
		// Highest inequal sum decides
		index = diff.length;
		while (--index >= 0) {
			int result = sum[index] - other.sum[index];
			if (result != 0) return switch (player) {
				case WHITE -> firstNonZeroDiff < 0;
				case BLACK -> firstNonZeroDiff > 0;
			} /* player is loosing */ ? result > 0 /* this has more stones */ : result < 0 /* other has more stones */;
		}
		return false; // Equal score is not better
	}

	@Override
	public StoneClassScore betterOf(StoneClassScore other, Player player) {
		return other == null || isBetterThan(other, player) ? self() : other;
	}

	int white(StoneClass stoneClass) {
		var index = stoneClass.ordinal();
		return (sum[index] + diff[index]) / 2;
	}

	int black(StoneClass stoneClass) {
		var index = stoneClass.ordinal();
		return (sum[index] - diff[index]) / 2;
	}

	@Override
	public String toString() {
		var result = new StringJoiner(", ", "[", "]");
		Player winningPlayer = null;
		int index = diff.length;
		while (--index != 0) {
			if (diff[index] != 0) {
				winningPlayer = diff[index] > 0 ? Player.WHITE : Player.BLACK;
				break;
			}
			if (sum[index] > 0)
				result.add(new StringBuilder().append(StoneClass.values()[index]).append(':').append(sum[index]));
		}
		if (winningPlayer != null) {
			result = new StringJoiner(", ", winningPlayer.stone.toString() + '[', "]");
			index = diff.length;
			while (--index >= 0) {
				if (sum[index] > 0) {
					var segment = new StringBuilder().append(StoneClass.values()[index]).append(':').append(sum[index]);
					var winnerDiff = switch (winningPlayer) {
						case WHITE -> diff[index];
						case BLACK -> -diff[index];
					};
					if (winnerDiff < 0) segment.append(winnerDiff);
					if (winnerDiff > 0) segment.append('+').append(winnerDiff);
					result.add(segment);
				}
			}
		}
		return result.toString();
	}

}
