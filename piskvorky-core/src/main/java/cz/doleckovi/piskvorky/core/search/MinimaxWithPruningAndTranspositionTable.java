package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.search.*;

import java.util.Iterator;
import java.util.Optional;

public class MinimaxWithPruningAndTranspositionTable<P extends Position<P>, S extends Score<S>> implements Search<P> {

	private final MoveGenerator<P> moveGenerator;
	private final Evaluator<P, S> evaluator;
	private final TranspositionTable<P, S> transpositionTable;

	public MinimaxWithPruningAndTranspositionTable(MoveGenerator<P> moveGenerator, Evaluator<P, S> evaluator,
	               TranspositionTable<P, S> transpositionTable)
	{
		this.moveGenerator = moveGenerator;
		this.evaluator = evaluator;
		this.transpositionTable = transpositionTable;
	}

	public MinimaxWithPruningAndTranspositionTable(MoveGenerator<P> moveGenerator, Evaluator<P, S> evaluator) {
		this(moveGenerator, evaluator, new NoOpTranspositionTable<>());
	}

	@Override
	public Optional<Move> search(P position, Player player, int depth)
			throws IllegalArgumentException, InterruptedException
	{
		if (depth <= 0)
			throw new IllegalArgumentException("Zero or less depth");
		var moves = moveGenerator.generateMoves(position, player).iterator();
		Move bestMove = null;
		S bestScore = null;
		while (moves.hasNext()) {
			var move = moves.next();
			S score;
			if (player == Player.WHITE) {
				score = maximize(position.afterMove(move), depth - 1, bestScore, null).betterOf(bestScore, Player.WHITE);
			} else {
				score = minimize(position.afterMove(move), depth - 1, null, bestScore).betterOf(bestScore, Player.BLACK);
			}
			if (score != bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		return Optional.ofNullable(bestMove);
	}

	S maximize(P whitePosition, int depth, S currentMax, S upperBound) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		var entry = transpositionTable.lookup(whitePosition, depth);
		if (entry != null) {
			if (entry.type() != EntryType.LOWER_BOUND)
				return entry.score();
			var lowerBound = entry.score();
			if (upperBound != null && upperBound.isBetterThan(lowerBound, Player.BLACK))
				return lowerBound; // Black would not allow this position
		}
		if (whitePosition.isTerminal()) {
			var score = evaluator.evaluate(whitePosition);
			transpositionTable.store(whitePosition, depth, score, EntryType.EXACT);
			return score;
		}
		boolean extension = (depth <= 0);
		Iterator<Move> moves;
		if (extension) {
			moves = moveGenerator.generateKillerMoves(whitePosition, Player.BLACK).iterator();
			if (!moves.hasNext()) {
				var score = evaluator.evaluate(whitePosition);
				transpositionTable.store(whitePosition, depth, score, EntryType.LOWER_BOUND);
				return score;
			}
		} else {
			moves = moveGenerator.generateMoves(whitePosition, Player.BLACK).iterator();
			if (!moves.hasNext()) {
				var score = evaluator.evaluateDraw(whitePosition);
				transpositionTable.store(whitePosition, depth, score, EntryType.EXACT);
				return score;
			}
		}
		var result = minimize(whitePosition.afterMove(moves.next()), depth - 1, currentMax, upperBound);
		EntryType type;
		if (result.isBetterThan(currentMax, Player.WHITE)) {
			currentMax = result;
			type = EntryType.EXACT;
		} else {
			// Minimize search could be pruned because it found value lower than currentMax
			// The exact value won't be higher => will store result to transposition table as upper bound
			type = EntryType.UPPER_BOUND;
		}
		if (upperBound == null || !upperBound.isBetterThan(result, Player.BLACK)) while (moves.hasNext()) {
			var score = minimize(whitePosition.afterMove(moves.next()), depth - 1, currentMax, upperBound);
			if (score.isBetterThan(result, Player.WHITE)) {
				result = score;
				if (result.isBetterThan(currentMax, Player.WHITE)) {
					type = EntryType.EXACT;
					if (upperBound != null && upperBound.isBetterThan(result, Player.BLACK))
						break; // Too good for white - black would not allow this position => prune the search
				}
			}
		}
		if (type == EntryType.EXACT && (extension || moves.hasNext()))
			type = EntryType.LOWER_BOUND; // Not all moves were searched
		transpositionTable.store(whitePosition, depth, result, type);
		return result;
	}

	S minimize(P blackPosition, int depth, S lowerBound, S currentMin) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		var lookup = transpositionTable.lookup(blackPosition, depth);
		if (lookup != null) {
			if (lookup.type() != EntryType.UPPER_BOUND)
				return lookup.score();
			var upperBound = lookup.score();
			if (lowerBound != null && lowerBound.isBetterThan(upperBound, Player.WHITE))
				return upperBound; // White would not allow this position
		}
		if (depth <= 0 || blackPosition.isTerminal()) {
			var score = evaluator.evaluate(blackPosition);
			transpositionTable.store(blackPosition, depth, score, EntryType.EXACT);
			return score;
		}
		Iterator<? extends Move> moves = moveGenerator.generateMoves(blackPosition, Player.WHITE).iterator();
		if (!moves.hasNext()) {
			var score = evaluator.evaluateDraw(blackPosition);
			transpositionTable.store(blackPosition, depth, score, EntryType.EXACT);
			return score;
		}
		var result = maximize(blackPosition.afterMove(moves.next()), depth - 1, lowerBound, currentMin);
		EntryType type;
		if (result.isBetterThan(currentMin, Player.BLACK)) {
			currentMin = result;
			type = EntryType.EXACT;
		} else {
			// Maximize search could be pruned because it found value higher than currentMin
			// The exact value won't be lower => will store result to transposition table as lower bound
			type = EntryType.LOWER_BOUND;
		}
		if (lowerBound == null || !lowerBound.isBetterThan(result, Player.WHITE)) while (moves.hasNext()) {
			var score = maximize(blackPosition.afterMove(moves.next()), depth - 1, lowerBound, currentMin);
			if (score.isBetterThan(result, Player.BLACK)) {
				result = score;
				if (result.isBetterThan(currentMin, Player.BLACK)) {
					type = EntryType.EXACT;
					if (lowerBound != null && lowerBound.isBetterThan(result, Player.WHITE))
						break; // Too good for black - white would not allow this position => prune the search
				}
			}
		}
		if (type == EntryType.EXACT && moves.hasNext())
			type = EntryType.UPPER_BOUND; // Not all moves were searched
		transpositionTable.store(blackPosition, depth, result, type);
		return result;
	}

}
