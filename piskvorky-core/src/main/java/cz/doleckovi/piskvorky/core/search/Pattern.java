package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.Piskvorky;
import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;

class Pattern {

	/** Number of player stones that make pattern. */
	public static final int LENGTH = Piskvorky.SIZE;
	/** Number of all possible patterns.
	 * <p>Number of combinations of stones ×2 - once if extra opponent stone is empty and once if it is present.</p>
	 */
	public static final int COUNT = (1 << LENGTH) * 2;
	/** Mask for extra <strong>opponent</strong> stone at start. */
	public static final int OPPONENT_MASK = 1 << LENGTH;
	/** Mask for player stones. */
	public static final int PLAYER_MASK = OPPONENT_MASK - 1;

	public static Pattern INVALID = new Pattern();

	final StoneClass[] classes;

	private Pattern() {
		classes = new StoneClass[LENGTH];
		Arrays.fill(classes, StoneClass.NONE);
	}

	Pattern(BitSet bits, StoneClass[] classes) {
		if (classes.length != LENGTH)
			throw new IllegalArgumentException("Invalid number of bits");
		if (bits.length() != LENGTH)
			throw new IllegalArgumentException("Invalid number of stone classes");
		Objects.requireNonNull(bits);
		this.classes = classes;
		var cardinality = bits.cardinality();
		for (var index = 0; index < LENGTH; ++index) {
			var classCardinality = classes[index].cardinality();
			if (bits.get(index)) {
				if (classCardinality != cardinality)
					throw new IllegalArgumentException(String.format("Stone at index %d has wrong cardinality", index));
			} else if (classCardinality != cardinality + 1)
				throw new IllegalArgumentException(String.format("Empty place at index %d has wrong cardinality", index));
		}
	}

	@Override
	public String toString() {
		var result = new StringBuilder(2 * LENGTH);
		for (var index = 0; index < LENGTH; ++index) {
			result.append(classes[index]);
		}
		return result.toString();
	}
}
