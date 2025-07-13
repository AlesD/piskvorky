package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.search.Evaluator;
import cz.doleckovi.piskvorky.core.board.Line;
import cz.doleckovi.piskvorky.core.board.PositionImpl;

import java.util.Arrays;
import java.util.Objects;

import static cz.doleckovi.piskvorky.core.search.Pattern.OPPONENT_MASK;
import static cz.doleckovi.piskvorky.core.search.Pattern.PLAYER_MASK;

public class EvaluatorImpl implements Evaluator<PositionImpl, ScoreImpl> {

	public static final EvaluatorImpl DEFAULT = new EvaluatorImpl(PatternHelper.defaultPatterns());

	private final Pattern[] patterns;

	EvaluatorImpl(Pattern[] patterns) {
		this.patterns = patterns;
	}

	@Override
	public ScoreImpl evaluate(PositionImpl position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ScoreImpl evaluateDraw(PositionImpl position) {
		throw new UnsupportedOperationException();
	}

	LineEvaluation evaluate(Line line) {
		var length = line.getLength();
		assert length >= Pattern.LENGTH;
		var whiteClasses = new StoneClass[length];
		var blackClasses = new StoneClass[length];
		// Setting both white and black stone makes the pattern INVALID
		var whiteBits = 1;
		var blackBits = 1;
		var readIndex = 0;
		// Push enough stones to make pattern
		while (readIndex < Pattern.LENGTH) {
			whiteBits <<= 1;
			blackBits <<= 1;
			switch (line.stone(readIndex++)) {
				case WHITE: whiteBits |= 1; break;
				case BLOCK: whiteBits |= 1; // Fall through
				case BLACK: blackBits |= 1;
			}
		}
		// Initialize values
		{
			var white = whiteBits & PLAYER_MASK;
			var black = blackBits & PLAYER_MASK;
			var whitePattern = black == 0 ? patterns[(blackBits & OPPONENT_MASK) | white] : Pattern.INVALID;
			var blackPattern = white == 0 ? patterns[(whiteBits & OPPONENT_MASK) | black] : Pattern.INVALID;
			for (int offset = 0; offset < Pattern.LENGTH; ++offset) {
				whiteClasses[offset] = whitePattern.classes[offset];
				blackClasses[offset] = blackPattern.classes[offset];
			}
		}
		var writeIndex = 0;
		// While there are more stones
		while (readIndex < length) {
			// Push stone
			whiteBits <<= 1;
			blackBits <<= 1;
			switch (line.stone(readIndex++)) {
				case WHITE: whiteBits |= 1; break;
				case BLOCK: whiteBits |= 1; // Fall through
				case BLACK: blackBits |= 1;
			}
			// Update values that are better
			var white = whiteBits & PLAYER_MASK;
			var black = blackBits & PLAYER_MASK;
			var whitePattern = black == 0 ? patterns[(blackBits & OPPONENT_MASK) | white] : Pattern.INVALID;
			var blackPattern = white == 0 ? patterns[(whiteBits & OPPONENT_MASK) | black] : Pattern.INVALID;
			var patternOffset = 0;
			var writeOffset = writeIndex++;
			do {
				var whiteClass = whitePattern.classes[patternOffset];
				if (whiteClasses[writeOffset].compareTo(whiteClass) < 0)
					whiteClasses[writeOffset] = whiteClass;
				var blackClass = blackPattern.classes[patternOffset];
				if (blackClasses[writeOffset].compareTo(blackClass) < 0)
					blackClasses[writeOffset] = blackClass;
				++patternOffset;
			} while (++writeOffset != readIndex);
			whiteClasses[writeOffset] = whitePattern.classes[patternOffset];
			blackClasses[writeOffset] = blackPattern.classes[patternOffset];
		}
		assert Arrays.stream(whiteClasses).allMatch(Objects::nonNull) : "Unevaluated white";
		assert Arrays.stream(blackClasses).allMatch(Objects::nonNull) : "Unevaluated black";
		return new LineEvaluation(line, whiteClasses, blackClasses);
	}

}

record LineEvaluation(Line line, StoneClass[] whites, StoneClass[] blacks) {}
