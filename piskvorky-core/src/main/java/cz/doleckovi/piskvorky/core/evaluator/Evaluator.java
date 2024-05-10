package cz.doleckovi.piskvorky.core.evaluator;

import cz.doleckovi.piskvorky.api.board.Row;
import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.Arrays;
import java.util.Objects;

import static cz.doleckovi.piskvorky.core.evaluator.Pattern.OPPONENT_MASK;
import static cz.doleckovi.piskvorky.core.evaluator.Pattern.PLAYER_MASK;

public class Evaluator {

	private static final Pattern[] INVALID;

	static {
		INVALID = new Pattern[Pattern.LENGTH];
		Arrays.fill(INVALID, Pattern.NONE);
	}

	public static final Evaluator DEFAULT = new Evaluator(PatternHelper.defaultValues());

	public final Pattern[][] patterns;

	public Evaluator(Pattern[][] patterns) {
		this.patterns = patterns;
	}

	public RowValue evaluate(Row row) {
		var length = row.getLength();
		Pattern[] whites;
		Pattern[] blacks;
		if (length < Pattern.LENGTH) {
			whites = new Pattern[length];
			Arrays.fill(whites, Pattern.NONE);
			blacks = whites;
		} else {
			whites = new Pattern[length];
			blacks = new Pattern[length];
			// Setting both white and black stone makes the pattern INVALID
			var whiteBits = 1;
			var blackBits = 1;
			var readIndex = 0;
			// Push enough stones to make pattern
			while (readIndex < Pattern.LENGTH) {
				whiteBits <<= 1;
				blackBits <<= 1;
				switch (row.getStone(readIndex++)) {
					case WHITE -> whiteBits |= 1;
					case BLACK -> blackBits |= 1;
				}
			}
			// Initialize values
			{
				var white = whiteBits & PLAYER_MASK;
				var black = blackBits & PLAYER_MASK;
				var whitePattern = black == 0 ? patterns[(blackBits & OPPONENT_MASK) | white] : INVALID;
				var blackPattern = white == 0 ? patterns[(whiteBits & OPPONENT_MASK) | black] : INVALID;
				for (int offset = 0; offset < Pattern.LENGTH; ++offset) {
					whites[offset] = whitePattern[offset];
					blacks[offset] = blackPattern[offset];
				}
			}
			var writeIndex = 0;
			// While there are more stones
			while (readIndex < length) {
				// Push stone
				whiteBits <<= 1;
				blackBits <<= 1;
				switch (row.getStone(readIndex)) {
					case WHITE -> whiteBits |= 1;
					case BLACK -> blackBits |= 1;
				}
				// Update values that are better
				var white = whiteBits & PLAYER_MASK;
				var black = blackBits & PLAYER_MASK;
				var whitePattern = black == 0 ? patterns[(blackBits & OPPONENT_MASK) | white] : INVALID;
				var blackPattern = white == 0 ? patterns[(whiteBits & OPPONENT_MASK) | black] : INVALID;
				var patternOffset = 0;
				var writeOffset = writeIndex++;
				do {
					var whiteValue = whitePattern[patternOffset];
					if (whites[writeOffset].compareTo(whiteValue) < 0)
						whites[writeOffset] = whiteValue;
					var blackValue = blackPattern[patternOffset];
					if (blacks[writeOffset].compareTo(blackValue) < 0)
						blacks[writeOffset] = blackValue;
					++patternOffset;
				} while (++writeOffset != readIndex);
				whites[writeOffset] = whitePattern[patternOffset];
				blacks[writeOffset] = blackPattern[patternOffset];
			}
		}
		assert Arrays.stream(whites).allMatch(Objects::nonNull) : "Unevaluated white";
		assert Arrays.stream(blacks).allMatch(Objects::nonNull) : "Unevaluated black";
		return new RowValue(whites, blacks, this);
	}

}
