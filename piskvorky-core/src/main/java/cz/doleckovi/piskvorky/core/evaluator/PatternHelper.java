package cz.doleckovi.piskvorky.core.evaluator;

import java.util.StringJoiner;

import cz.doleckovi.piskvorky.api.board.Stone;

import static cz.doleckovi.piskvorky.core.evaluator.Pattern.OPPONENT_MASK;
import static cz.doleckovi.piskvorky.core.evaluator.Pattern.PLAYER_MASK;

public class PatternHelper {

	static final int bits(String stones) {
		if (stones.length() != Pattern.LENGTH + 1)
			throw new IllegalArgumentException("Pattern must contain exactly " + (Pattern.LENGTH + 1) + " stones");
		var result = 0;
		var mask = OPPONENT_MASK;
		for (int index = 0; index <= Pattern.LENGTH; ++index) {
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

	static final int bitCount(int bits) {
		int result = 0;
		while (bits != 0) {
			result += bits & 1;
			bits >>>= 1;
		}
		return result;
	}

	public static final Pattern[][] defaultValues() {
		var result = new Pattern[Pattern.COUNT][];
		// Fill from pattern with all stones to pattern with no stones => pattern with extra stone is always defined
		for (int bits = PLAYER_MASK; bits >= 0; --bits) {
			var values = new Pattern[Pattern.LENGTH];
            for (int index = 0, mask = 1 << Pattern.LENGTH >> 1; index < values.length; ++index, mask >>= 1) {
				values[index] = ((bits & mask) == 0) ? result[bits | mask][index] : switch (bitCount(bits)) {
					case 0 -> Pattern.NONE;
					case 1 -> Pattern.ONE;
					case 2 -> Pattern.TWO;
					case 3 -> Pattern.THREE;
					case 4 -> Pattern.FOUR;
					case 5 -> Pattern.FIVE_IN_ROW;
					default -> throw new IllegalStateException("Unexpected number of bits in pattern");
				};
			}
			result[bits] = values;
			result[bits | OPPONENT_MASK] = values;
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
		var defaultValues = defaultValues();
		for (int bits = 0; bits < defaultValues.length; ++bits) {
			StringJoiner joiner = new StringJoiner(",");
			var values = defaultValues[bits];
			for (var index = 0; index < values.length; ++index)
				joiner.add(values[index].toString());
			System.out.println(new StringBuilder(toString(bits)).append('=').append(joiner));
		}
	}

}
