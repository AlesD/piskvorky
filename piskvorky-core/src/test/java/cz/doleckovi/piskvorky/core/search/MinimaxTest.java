package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.search.Player;
import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.search.Evaluator;
import cz.doleckovi.piskvorky.api.search.Score;
import cz.doleckovi.piskvorky.api.search.Move;
import cz.doleckovi.piskvorky.api.search.MoveGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MinimaxTest<P extends Position<P>, S extends Score<S>> {

	@Mock P position;
	@Mock MoveGenerator<P> moveGenerator;
	@Mock Evaluator<P, S> evaluator;
	@Mock TranspositionTable<P, S> transpositionTable;
	@InjectMocks
	MinimaxWithPruningAndTranspositionTable<P, S> minimax;

	@Test
	void exactTranspositionTableHitPreventsMinimizeSearch(@Mock S exact) throws InterruptedException {
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(exact, EntryType.EXACT));

		assertThat(minimax.minimize(position, 1, null, null))
				.isSameAs(exact);

		verifyNoInteractions(moveGenerator);
		verifyNoInteractions(evaluator);
		verify(transpositionTable, never()).store(any(), anyInt(), any(), any());
	}

	@Test
	void exactTranspositionTableHitPreventsMaximizeSearch(@Mock S exact) throws InterruptedException {
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(exact, EntryType.EXACT));

		assertThat(minimax.maximize(position, 1, null, null))
				.isSameAs(exact);

		verifyNoInteractions(moveGenerator);
		verifyNoInteractions(evaluator);
		verify(transpositionTable, never()).store(any(), anyInt(), any(), any());
	}

	@Test
	void lowerBoundTranspositionTableHitPreventsMinimizeSearch(@Mock S lowerBound) throws InterruptedException {
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(lowerBound, EntryType.LOWER_BOUND));

		// Minimizing search can't find score lower than lowerBound => return lowerBound without search
		assertThat(minimax.minimize(position, 1, null, null))
				.isSameAs(lowerBound);

		verifyNoInteractions(moveGenerator);
		verifyNoInteractions(evaluator);
		verify(transpositionTable, never()).store(any(), anyInt(), any(), any());
	}

	@Test
	void lowerBoundTranspositionTableHitDoesNotPreventMaximizeSearchWhenUpperBoundIsNotKnown(
			@Mock S lowerBound, @Mock S score) throws InterruptedException
	{
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(lowerBound, EntryType.LOWER_BOUND));
		when(evaluator.evaluate(position))
				.thenReturn(score);

		// Maximizing search can find value higher than lower bound
		assertThat(minimax.maximize(position, 1, null,null))
				.isSameAs(score);

		verify(moveGenerator).generateMoves(position, Player.BLACK);
		verify(transpositionTable).store(position, 1, score, EntryType.EXACT);
	}

	@Test
	void lowerBoundTranspositionTableHitPreventsMaximizeSearchWhenLowerBoundIsHigherOrEqualToUpperBound(
			@Mock S lowerBound, @Mock S upperBound) throws InterruptedException
	{
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(lowerBound, EntryType.LOWER_BOUND));
		when(upperBound.isBetterThan(lowerBound, Player.BLACK))
				.thenReturn(true); // Lower bound is same as upper bound

		// Black can force other position with upperBound score
		// Search will produce same or higher score - which is worse for black
		// => Returning lowerBound is enough to black avoid this position
		assertThat(minimax.maximize(position, 1, null, upperBound))
				.isSameAs(lowerBound);

		verifyNoInteractions(moveGenerator);
		verifyNoInteractions(evaluator);
		verify(transpositionTable, never()).store(any(), anyInt(), any(), any());
	}

	@Test
	void lowerBoundTranspositionTableHitDoesNotPreventMaximizeSearchWhenLowerBoundIsLessThanUpperBound(
			@Mock S lowerBound, @Mock S upperBound, @Mock S score) throws InterruptedException
	{
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(lowerBound, EntryType.LOWER_BOUND));
		when(lowerBound.isBetterThan(upperBound, Player.WHITE))
				.thenReturn(false); // Lower bound is less/better than upper bound
		when(evaluator.evaluate(position))
				.thenReturn(score);

		// Search can find value between lower bound and upper bound
		assertThat(minimax.maximize(position, 1, null, upperBound))
				.isSameAs(score);

		verify(moveGenerator).generateMoves(position, Player.BLACK);
		verify(transpositionTable).store(position, 1, score, EntryType.EXACT);
	}

	@Test
	void upperBoundTranspositionTableHitPreventsMaximizeSearch(@Mock S upperBound) throws InterruptedException {
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(upperBound, EntryType.UPPER_BOUND));

		// Maximizing search can't find score higher than upperBound => return upperBound without search
		assertThat(minimax.maximize(position, 1, null, null))
				.isSameAs(upperBound);

		verifyNoInteractions(moveGenerator);
		verifyNoInteractions(evaluator);
		verify(transpositionTable, never()).store(any(), anyInt(), any(), any());
	}

	@Test
	void upperBoundTranspositionTableHitDoesNotPreventMinimizeSearchWhenLowerBoundIsNotKnown(
			@Mock S upperBound, @Mock S score) throws InterruptedException
	{
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(upperBound, EntryType.UPPER_BOUND));
		when(evaluator.evaluate(position))
				.thenReturn(score);

		// Minimizing search can find value lower than upper bound
		assertThat(minimax.minimize(position, 1, null,null))
				.isSameAs(score);

		verify(moveGenerator).generateMoves(position, Player.WHITE);
		verify(transpositionTable).store(position, 1, score, EntryType.EXACT);
	}

	@Test
	void upperBoundTranspositionTableHitPreventsMinimizeSearchWhenUpperBoundIsLowerOrEqualToLowerBound(
			@Mock S upperBound, @Mock S lowerBound) throws InterruptedException
	{
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(upperBound, EntryType.UPPER_BOUND));
		when(upperBound.isBetterThan(lowerBound, Player.WHITE))
				.thenReturn(false); // Upper bound is same as lower bound

		// White can force other position with whiteBest score
		// Search will produce same or lower score - which is worse for white
		// => Returning upperBound is enough to white avoid this position
		assertThat(minimax.minimize(position, 1, lowerBound, null))
				.isSameAs(upperBound);
		assertThat(minimax.minimize(position, 1, lowerBound, null))
				.isSameAs(upperBound);

		verifyNoInteractions(moveGenerator);
		verifyNoInteractions(evaluator);
		verify(transpositionTable, never()).store(any(), anyInt(), any(), any());
	}

	@Test
	void upperBoundTranspositionTableHitDoesNotPreventMinimizeSearchWhenUpperBoundIsMoreThanLowerBound(
			@Mock S upperBound, @Mock S lowerBound, @Mock S score) throws InterruptedException
	{
		when(transpositionTable.lookup(position, 1))
				.thenReturn(new TranspositionTableEntry<>(upperBound, EntryType.UPPER_BOUND));
		when(upperBound.isBetterThan(lowerBound, Player.WHITE))
				.thenReturn(true); // Upper bound is more/better than lower bound
		when(evaluator.evaluate(position))
				.thenReturn(score);

		// Search can find value between lower bound and upper bound (white would allow it)
		assertThat(minimax.minimize(position, 1, lowerBound, null))
				.isSameAs(score);

		verify(moveGenerator).generateMoves(position, Player.WHITE);
		verify(transpositionTable).store(position, 1, score, EntryType.EXACT);
	}

	@Test
	void zeroDepthMaximizeTriggersEvaluation(@Mock S score) throws InterruptedException {
		when(evaluator.evaluate(position))
				.thenReturn(score);

		assertThat(minimax.maximize(position, 0, null, null))
				.isSameAs(score);

		verifyNoInteractions(moveGenerator);
		verify(transpositionTable).store(position, 0, score, EntryType.EXACT);
	}

	@Test
	void zeroDepthMinimizeTriggersEvaluation(@Mock S score) throws InterruptedException {
		when(evaluator.evaluate(position))
				.thenReturn(score);

		assertThat(minimax.minimize(position, 0, null, null))
				.isSameAs(score);

		verifyNoInteractions(moveGenerator);
		verify(transpositionTable).store(position, 0, score, EntryType.EXACT);
	}

	@Test
	void noBlackMovesInMaximizeTriggersEvaluation(@Mock S score) throws InterruptedException {
		when(evaluator.evaluate(position))
				.thenReturn(score);

		assertThat(minimax.maximize(position, 1, null, null))
				.isSameAs(score);

		verify(moveGenerator).generateMoves(position, Player.BLACK);
		verify(transpositionTable).store(position, 1, score, EntryType.EXACT);
	}

	@Test
	void noWhiteMovesInMinimizeTriggersEvaluation(@Mock S score) throws InterruptedException {
		when(evaluator.evaluate(position))
				.thenReturn(score);

		assertThat(minimax.minimize(position, 1, null, null))
				.isSameAs(score);

		verify(moveGenerator).generateMoves(position, Player.WHITE);
		verify(transpositionTable).store(position, 1, score, EntryType.EXACT);
	}

	@Test
	void cutoffOnFirstMoveInMaximize(@Mock Move move1, @Mock Move move2, @Mock P position1, @Mock S currentMax,
			@Mock S upperBound, @Mock S score) throws InterruptedException
	{
		when(moveGenerator.generateMoves(position, Player.BLACK))
				.thenReturn(List.of(move1, move2));
		when(position.afterMove(move1))
				.thenReturn(position1);
		when(evaluator.evaluate(position1))
				.thenReturn(score);
		when(score.isBetterThan(currentMax, Player.WHITE))
				.thenReturn(false);
		when(score.isBetterThan(upperBound, Player.WHITE))
				.thenReturn(true); // cutoff

		assertThat(minimax.maximize(position, 1, currentMax, upperBound))
				.isSameAs(score);

		verify(transpositionTable).store(position1, 0, score, EntryType.EXACT);
		verify(transpositionTable).store(position, 1, score, EntryType.UPPER_BOUND);
	}

	@Test
	void cutoffOnFirstMoveInMinimize(@Mock Move move1, @Mock Move move2, @Mock P position1, @Mock S currentMin,
			@Mock S lowerBound, @Mock S score) throws InterruptedException
	{
		when(moveGenerator.generateMoves(position, Player.WHITE))
				.thenReturn(List.of(move1, move2));
		when(position.afterMove(move1))
				.thenReturn(position1);
		when(evaluator.evaluate(position1))
				.thenReturn(score);
		when(score.isBetterThan(currentMin, Player.BLACK))
				.thenReturn(false);
		when(score.isBetterThan(lowerBound, Player.BLACK))
				.thenReturn(true); // cutoff

		assertThat(minimax.minimize(position, 1, lowerBound, currentMin))
				.isSameAs(score);

		verify(transpositionTable).store(position1, 0, score, EntryType.EXACT);
		verify(transpositionTable).store(position, 1, score, EntryType.LOWER_BOUND);
	}

}