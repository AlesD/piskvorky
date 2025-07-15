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

	static Entry<String, ScoreImpl> score(String name, int whiteStones, int blackStones) {
		return new SimpleImmutableEntry<>(name, new ScoreImpl(new int[] {whiteStones}, new int[] {blackStones}));
	}

	static Entry<String, ScoreImpl> score(String name, int lowClassWhiteStoneCount, int lowClassBlackStoneCount,
			int highClassWhiteStoneCount, int highClassBlackStoneCount) {
		return new SimpleImmutableEntry<>(name, new ScoreImpl(
				new int[] {lowClassWhiteStoneCount, highClassWhiteStoneCount},
				new int[] {lowClassBlackStoneCount, highClassBlackStoneCount}));
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

	private static Stream<Arguments> unequalScores() {
		var goodForWhite = score("1:3, 2:1", 1, 3, 2, 1);
		var goodForBlack = score("2:1, 1:2", 2, 1, 1, 2);
		var betterForWhite = score("1:2, 2:1", 1, 2, 2, 1);
		var littleMoreHighClassStones = score("0:4, 3:1", 0, 4, 3, 1);
		var wayMoreLowClassStones = score("0:4, 3:1", 0, 4, 3, 1);
		var moreLowClassStones = score("2:1, 2:1", 2, 1, 2, 1);
		var fewerLowClassStones = score("2:2, 3:2", 2, 2, 3, 2);
		return Stream.of(
				// Winning vs.loosing is easy to decide (higher class of stones decides)
				arguments(goodForWhite, goodForBlack, true, false, false, true),
				// Winning by little vs winning by far is also easy
				arguments(goodForWhite, betterForWhite, false, true, true, false),
				// Having little more high class stones is still better even if opponent has (way) more stones of lower class
				arguments(littleMoreHighClassStones, wayMoreLowClassStones, true, false, false, true),
				// What is better - having 3 vs. 2 high class stones or 2 vs. 1 high class stone?
				// Both is +1 difference => look at lower class of stones to decide
				arguments(moreLowClassStones, fewerLowClassStones, true, false, false, true)
		).flatMap(Function.identity());
	}

	private static Stream<Arguments> scoresWithEqualDifferences() {
		var winningWithLessStones = score("2:4, 2:1", 2, 4, 2, 1);
		var winningWithMoreStones = score("1:3, 3:2", 1, 3, 3, 2);
		var loosingWithLessStones = score("2:4, 2:1", 2, 4, 2, 1);
		var loosingWithMoreStones = score("1:3, 3:2", 1, 3, 3, 2);
		// These are more tricky to decide ...
		return Stream.of(
				// With 2 scores that both have +1 high class stones and -2 low class stones ...
				// Avoid gaining unnecessary stones if opponent gains as well => fewer stones ~ faster win
				arguments(winningWithLessStones, winningWithMoreStones, true, false, false, true),
				// However if loosing use different approach => more stones ~ slower defeat
				// In other words - give opponent chance to make mistake
				arguments(loosingWithLessStones, loosingWithMoreStones, false, true, true, false)
		).flatMap(Function.identity());
	}

	private static Stream<Arguments> scoresWithZeroDifferences() {
		return Stream.of(

		);//.flatMap(Function.identity());
	}

	private static Stream<Arguments> terminalScores() {
		return Stream.of(

		);//.flatMap(Function.identity());
	}

	@ParameterizedTest
	@MethodSource
	void unequalScores(Player player, ScoreImpl score1, ScoreImpl score2, boolean expectedResult) {
		assertThat(score1.isBetterThan(score2, player)).isEqualTo(expectedResult);
	}

	@ParameterizedTest
	@MethodSource
	void scoresWithEqualDifferences(Player player, ScoreImpl score1, ScoreImpl score2, boolean expectedResult) {
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
