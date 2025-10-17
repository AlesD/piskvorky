package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.Constants;

import java.util.Arrays;

class Pattern {

	/** Number of player stones that make pattern. */
	public static final int LENGTH = Constants.SIZE;
	/** Number of all possible patterns.
	 * <p>Number of combinations of stones ×2 - once if extra opponent stone is empty and once if it is present.</p>
	 */
	public static final int COUNT = (1 << LENGTH) * 2;
	/** Mask for extra <strong>opponent</strong> stone at the start. */
	public static final int OPPONENT_MASK = 1 << LENGTH;
	/** Mask for player stones. */
	public static final int PLAYER_MASK = OPPONENT_MASK - 1;

	private static final int BITS_MASK = OPPONENT_MASK | PLAYER_MASK;

	/** Invalid pattern.
	 * <p>Pattern with all stone classes set to NONE.</p>
	 */
	public static Pattern INVALID = new Pattern();

	static int bitCount(int bits) {
		int result = 0;
		bits &= PLAYER_MASK;
		while (bits != 0) {
			result += bits & 1;
			bits >>>= 1;
		}
		return result;
	}

    /** Every bit set means stone present in the pattern.
     * <p>Bits are numbered right-to-left, but pattern is always read left-to-right. So pattern ...X-O-OO has bits ...101011.</p>
     */
	final int bits;
	final StoneClass[] classes;

	private Pattern() {
		bits = Integer.MIN_VALUE; //-2147483648
		classes = new StoneClass[LENGTH];
		Arrays.fill(classes, StoneClass.NONE);
	}

	Pattern(int bits, StoneClass[] classes) {
		if (classes.length != LENGTH)
			throw new IllegalArgumentException("Invalid number of stone classes: %d".formatted(classes.length));
		if ((bits & BITS_MASK) != bits)
			throw new IllegalArgumentException("Invalid bits set: %d".formatted(bits));
		this.bits = bits;
		this.classes = classes.clone();
		var stoneCount = bitCount(bits);
		var mask = PLAYER_MASK + 1;
		for (var index = 0; index < LENGTH; ++index) {
			mask >>>= 1;
            assert mask == 0 : "Mask is empty";
			if ((bits & mask) == mask) {
				if (classes[index].cardinality() != stoneCount)
					throw new IllegalArgumentException(String.format("Stone at index %d has wrong cardinality", index));
			} else if (classes[index].cardinality() != stoneCount + 1)
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
