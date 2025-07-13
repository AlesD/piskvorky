package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.BitSet;

import static cz.doleckovi.piskvorky.core.search.Pattern.*;

public class PatternHelper {

	static int bits(String stones) {
		if (stones.length() != LENGTH)
			throw new IllegalArgumentException(String.format("Pattern must contain exactly %d stones", LENGTH));
		var result = 0;
		var mask = OPPONENT_MASK;
		for (int index = 0; index <= LENGTH; ++index) {
			var stone = Stone.valueOf(stones.charAt(index));
			if ((stone == Stone.BLACK) && (mask != OPPONENT_MASK))
				throw new IllegalArgumentException("Black stone inside pattern");
			if ((stone == Stone.WHITE) && (mask & PLAYER_MASK) == 0)
				throw new IllegalArgumentException("White stone at pattern start");
			if (stone != Stone.EMPTY)
				result |= mask;
			mask >>>= 1;
		}
		return result;
	}

	static int bitCount(int bits) {
		int result = 0;
		while (bits != 0) {
			result += bits & 1;
			bits >>>= 1;
		}
		return result;
	}

	private static StoneClass stoneClass(int cardinality) {
		return switch (cardinality) {
			case 1 -> StoneClass.ONE;
			case 2 -> StoneClass.TWO;
			case 3 -> StoneClass.THREE;
			case 4 -> StoneClass.FOUR;
			case 5 -> StoneClass.FIVE_IN_ROW;
			default -> StoneClass.NONE;
		};
	}

	private static StoneClass emptyClass(int cardinality) {
		return switch (cardinality) {
			case 0 -> StoneClass.ONE;
			case 1 -> StoneClass.TWO;
			case 2 -> StoneClass.THREE;
			case 3 -> StoneClass.FOUR;
			case 4 -> StoneClass.FIVE_IN_ROW;
			default -> StoneClass.NONE;
		};
	}

	private static BitSet toBits(int integer) {
		var result = new BitSet(LENGTH);
		int index = 0;
		while (integer != 0 && index < LENGTH) {
			result.set(index++, (integer & 1) == 1);
			integer >>= 1;
		}
		return result;
	}

	static Pattern[] defaultPatterns() {
		var result = new Pattern[COUNT];
		// Fill from pattern with all stones to pattern with no stones => pattern with extra stone is always defined
		var classes = new StoneClass[LENGTH];
		for (int patternId = PLAYER_MASK; patternId >= 0; --patternId) {
			var bits = toBits(patternId);
			var stoneClass = stoneClass(bits.cardinality());
			var emptyClass = emptyClass(stoneClass.cardinality());
			for (int index = 0; index < LENGTH; ++index)
				classes[index] = bits.get(index) ? stoneClass : emptyClass;
			var pattern = new Pattern(bits, classes);
			result[patternId] = pattern;
			result[patternId | OPPONENT_MASK] = pattern;
		}
		return result;
	}

	public static String toString(int bits) {
		var result = new StringBuilder();
		for (int mask = OPPONENT_MASK; mask != 0; mask >>= 1) {
			result.append(switch (bits & mask) {
				case OPPONENT_MASK -> Stone.BLACK;
				case 0 -> Stone.EMPTY;
				default -> Stone.WHITE;
			});
		}
		return result.toString();
	}

	public static void main(String[] args) {
		var patterns = defaultPatterns();
		for (int bits = 0; bits < patterns.length; ++bits) {
			var pattern = patterns[bits];
			System.out.println(toString(bits) + '=' + pattern);
		}
	}

}
