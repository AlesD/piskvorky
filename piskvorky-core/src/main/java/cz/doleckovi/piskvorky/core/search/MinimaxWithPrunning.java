package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.evaluation.Evaluator;
import cz.doleckovi.piskvorky.api.evaluation.Score;
import cz.doleckovi.piskvorky.api.search.*;

import java.util.Optional;

public class MinimaxWithPrunning<P extends Position<P>, S extends Score<S>> implements Search<P> {

	private final MoveGenerator<P> moveGenerator;
	private final Evaluator<P, S> evaluator;

	public MinimaxWithPrunning(MoveGenerator<P> moveGenerator, Evaluator<P, S> evaluator) {
		this.moveGenerator = moveGenerator;
		this.evaluator = evaluator;
	}

	@Override
	public Optional<Move> search(P position, Side side, int depth)
			throws IllegalArgumentException, InterruptedException
	{
		if (--depth < 0) throw new IllegalArgumentException("Zero or less depth");
		var moves = moveGenerator.generateMoves(position, side).iterator();
		Move bestMove = null;
		S bestScore = null;
		while (moves.hasNext()) {
			var move = moves.next();
			S score;
			if (side == Side.WHITE) {
				score = search(position.afterMove(move), depth, bestScore, null, Side.BLACK).betterOf(bestScore, Side.WHITE);
			} else {
				score = search(position.afterMove(move), depth, null, bestScore, Side.WHITE).betterOf(bestScore, Side.BLACK);
			}
			if (score != bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		return Optional.ofNullable(bestMove);
	}

	S search(P position, int depth, S lowerBound, S upperBound, Side side) throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
		if (position.isTerminal()) {
			var score = evaluator.evaluate(position);
			assert score.isTerminal();
			return score;
		}
		if (--depth < 0)
			return evaluator.evaluate(position);
		var moves = moveGenerator.generateMoves(position, side.opposite()).iterator();
		if (!moves.hasNext())
			return evaluator.evaluate(position);
		S result = null;
		do {
			result = search(position.afterMove(moves.next()), depth, lowerBound, upperBound, side.opposite()).betterOf(result,
					side);
			if (side == Side.WHITE) {
				if (upperBound != null && upperBound.isBetterThan(result, Side.BLACK))
					break;
				lowerBound = result.betterOf(lowerBound, Side.WHITE);
			} else {
				if (lowerBound != null && lowerBound.isBetterThan(result, Side.WHITE))
					break;
				upperBound = result.betterOf(upperBound, Side.BLACK);
			}
		} while (moves.hasNext());
		return result;
	}

}
