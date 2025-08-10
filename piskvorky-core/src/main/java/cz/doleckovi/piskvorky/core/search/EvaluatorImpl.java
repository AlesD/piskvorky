package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.search.Evaluator;
import cz.doleckovi.piskvorky.core.board.Line;
import cz.doleckovi.piskvorky.core.board.PositionImpl;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import static cz.doleckovi.piskvorky.core.search.Pattern.OPPONENT_MASK;
import static cz.doleckovi.piskvorky.core.search.Pattern.PLAYER_MASK;

class EvaluatorImpl implements Evaluator<PositionImpl, StoneClassScore> {

	// TODO For fields with WHITE stone (or BLOCK) blacks contain NONE (and vice versa)
	//      => We need two arrays only for EMPTY fields.
	//      If empty fields are not used for evaluation we can merge whites and blacks
	record LineEvaluation(Line line, StoneClass[] whites, StoneClass[] blacks) {}

	public static final EvaluatorImpl DEFAULT = new EvaluatorImpl(PatternHelper.defaultPatterns());

	private final Pattern[] patterns;

	EvaluatorImpl(Pattern[] patterns) {
		this.patterns = patterns;
	}

	@Override
	public StoneClassScore evaluate(PositionImpl position) {
		var white = new int[StoneClass.values().length];
		var black = new int[white.length];
		var lines = position.lines();
		while (lines.hasNext()) {
			var line = lines.next();
			var evaluation = evaluate(line);
			for (var index = line.length() - 1; index >= 0; --index) {
				switch (line.stone(index)) {
					case WHITE -> ++white[evaluation.whites[index].ordinal()];
					case BLACK -> ++black[evaluation.blacks[index].ordinal()];
				}
			}
		}
		return new StoneClassScore(white, black);
	}

	LineEvaluation evaluate(Line line) {
		var length = line.length();
		assert length >= Pattern.LENGTH;
		var whiteClasses = new StoneClass[length];
		var blackClasses = new StoneClass[length];
		// Setting both white and black stone makes the pattern INVALID
		var whiteBits = 1;
		var blackBits = 1;
		var readIndex = 0;
		// Push enough stones to make a pattern
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
		// While there are more stones
		while (readIndex < length) {
			// Push stone
			whiteBits <<= 1;
			blackBits <<= 1;
			switch (line.stone(readIndex)) {
				case WHITE: whiteBits |= 1; break;
				case BLOCK: whiteBits |= 1; // Fall through
				case BLACK: blackBits |= 1;
			}
			// Update values that are better
			var white = whiteBits & PLAYER_MASK;
			var black = blackBits & PLAYER_MASK;
			var whitePattern = black == 0 ? patterns[(blackBits & OPPONENT_MASK) | white] : Pattern.INVALID;
			var blackPattern = white == 0 ? patterns[(whiteBits & OPPONENT_MASK) | black] : Pattern.INVALID;
			var offset = Pattern.LENGTH - 1;
			var writeIndex = readIndex;
			whiteClasses[writeIndex] = whitePattern.classes[offset];
			blackClasses[writeIndex] = blackPattern.classes[offset];
			while (--offset >= 0) {
				--writeIndex;
				var whiteClass = whitePattern.classes[offset];
				if (whiteClasses[writeIndex].compareTo(whiteClass) < 0)
					whiteClasses[writeIndex] = whiteClass;
				var blackClass = blackPattern.classes[offset];
				if (blackClasses[writeIndex].compareTo(blackClass) < 0)
					blackClasses[writeIndex] = blackClass;
			}
			++readIndex;
		}
		assert Arrays.stream(whiteClasses).allMatch(Objects::nonNull) : "Unevaluated white";
		assert Arrays.stream(blackClasses).allMatch(Objects::nonNull) : "Unevaluated black";
		return new LineEvaluation(line, whiteClasses, blackClasses);
	}

}
