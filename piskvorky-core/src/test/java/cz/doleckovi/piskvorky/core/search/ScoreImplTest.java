package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.search.Player;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

class ScoreImplTest implements WithAssertions {

	void assertions(ScoreImpl score1, ScoreImpl score2, boolean score1betterForWhite, boolean score1betterForBlack,
			boolean score2betterForWhite, boolean score2betterForBlack)
	{
		assertThat(score1.isBetterThan(score2, Player.WHITE)).isEqualTo(score1betterForWhite);
		assertThat(score1.isBetterThan(score2, Player.BLACK)).isEqualTo(score1betterForBlack);
		assertThat(score2.isBetterThan(score1, Player.WHITE)).isEqualTo(score2betterForWhite);
		assertThat(score2.isBetterThan(score1, Player.BLACK)).isEqualTo(score2betterForBlack);
	}

	ScoreImpl score(int whiteStones, int blackStones) {
		return new ScoreImpl(new int[] {whiteStones}, new int[] {blackStones});
	}

	static Entry<String, ScoreImpl> score(String name, int whiteStones, int blackStones) {
		return new SimpleImmutableEntry<>(name, new ScoreImpl(new int[] {whiteStones}, new int[] {blackStones}));
	}

	private static Arguments arguments(Player player, Entry<String, ScoreImpl> score1, Entry<String, ScoreImpl> score2, boolean result)
	{
		var name = switch (result) {
			case true -> "For %s player score '%s' should be better than score '%s'";
			case false -> "For %s player score '%s' should NOT be better than score '%s'";
		};
		return Arguments.argumentSet(name.formatted(player, score1.getKey(), score2.getKey()),
				player, score1.getValue(), score2.getValue(), result);
	}

	private static Stream<Arguments> arguments(Entry<String, ScoreImpl> score1, Entry<String, ScoreImpl> score2,
			boolean score1betterForWhite, boolean score1betterForBlack,
			boolean score2betterForWhite, boolean score2betterForBlack)
	{
		return Stream.of(
				arguments(Player.WHITE, score1, score2, score1betterForWhite),
				arguments(Player.BLACK, score1, score2, score1betterForBlack),
				arguments(Player.WHITE, score2, score1, score2betterForWhite),
				arguments(Player.BLACK, score2, score1, score2betterForBlack)
		);
	}

	private static Stream<Arguments> scoresWithUnequalDifferences() {
		var goodForWhite = score("good for white", 2, 1);
		var goodForBlack = score("good for black", 1, 2);
		var betterForWhite = score("better for white", 3, 1);
		return Stream.of(
				arguments(goodForWhite, goodForBlack, true, false, false, true),
				arguments(goodForWhite, betterForWhite, false, true, true, false)
		).flatMap(Function.identity());
	}

	private static Stream<Arguments> scoresWithEqualDifferences() {
		// Both players have more stones in 3:2 position
		// White player has +1 advantage in both positions
		// => Both players prefer positions with more stones
		// For winning player it is obvious
		// but for loosing player (if he was on move) it provides more threats
		var white21 = score("white 2:1", 2, 1);
		var white32 = score("white 3:2", 3, 2);
		return Stream.of(
				arguments(white21, white32, false, false, true, true)
		).flatMap(Function.identity());
	}

	@ParameterizedTest
	@MethodSource("scoresWithUnequalDifferences")
	void unequalScores(Player player, ScoreImpl score1, ScoreImpl score2, boolean expectedResult) {
		assertThat(score1.isBetterThan(score2, player)).isEqualTo(expectedResult);
	}

	@Test
	void goodAndBetter() {
		var goodWhite = new ScoreImpl(false, new int[] {2}, new int[] {1});
		var betterWhite = new ScoreImpl(false, new int[] {3}, new int[] {1});
		assertThat(goodWhite.isBetterThan(betterWhite, Player.WHITE)).isFalse();
		assertThat(goodWhite.isBetterThan(betterWhite, Player.BLACK)).isTrue();
		assertThat(betterWhite.isBetterThan(goodWhite, Player.WHITE)).isTrue();
		assertThat(betterWhite.isBetterThan(goodWhite, Player.BLACK)).isFalse();
	}

}
