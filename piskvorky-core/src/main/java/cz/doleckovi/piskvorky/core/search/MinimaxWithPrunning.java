package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Position;
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
	public Optional<Move> search(P position, Player player, int depth)
			throws IllegalArgumentException, InterruptedException
	{
		if (--depth < 0) throw new IllegalArgumentException("Zero or less depth");
		var moves = moveGenerator.generateMoves(position, player).iterator();
		Move bestMove = null;
		S bestScore = null;
		while (moves.hasNext()) {
			var move = moves.next();
			S score;
			if (player == Player.WHITE) {
				score = search(position.afterMove(move), depth, bestScore, null, Player.BLACK).betterOf(bestScore, Player.WHITE);
			} else {
				score = search(position.afterMove(move), depth, null, bestScore, Player.WHITE).betterOf(bestScore, Player.BLACK);
			}
			if (score != bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		return Optional.ofNullable(bestMove);
	}

	S search(P position, int depth, S lowerBound, S upperBound, Player player) throws InterruptedException {
		if (Thread.interrupted()) throw new InterruptedException();
		if (position.isTerminal()) {
			var score = evaluator.evaluate(position);
			assert score.isTerminal();
			return score;
		}
		if (--depth < 0)
			return evaluator.evaluate(position);
		var moves = moveGenerator.generateMoves(position, player.opponent()).iterator();
		if (!moves.hasNext())
			return evaluator.evaluate(position);
		S result = null;
		do {
			result = search(position.afterMove(moves.next()), depth, lowerBound, upperBound, player.opponent()).betterOf(result, player);
			if (player == Player.WHITE) {
				if (upperBound != null && upperBound.isBetterThan(result, Player.BLACK))
					break;
				lowerBound = result.betterOf(lowerBound, Player.WHITE);
			} else {
				if (lowerBound != null && lowerBound.isBetterThan(result, Player.WHITE))
					break;
				upperBound = result.betterOf(upperBound, Player.BLACK);
			}
		} while (moves.hasNext());
		return result;
	}

}
