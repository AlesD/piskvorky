package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.search.*;

import java.util.Optional;

public class Minimax<P extends Position<P>, S extends Score<S>> implements Search<P> {

	private final MoveGenerator<P> moveGenerator;
	private final Evaluator<P, S> evaluator;

	public Minimax(MoveGenerator<P> moveGenerator, Evaluator<P, S> evaluator) {
		this.moveGenerator = moveGenerator;
		this.evaluator = evaluator;
	}

	@Override
	public Optional<Move> search(P position, Player player, int depth)
			throws IllegalArgumentException, InterruptedException
	{
		if (--depth < 0)
			throw new IllegalArgumentException("Zero or negative depth");
		var moves = moveGenerator.generateMoves(position, player).iterator();
		Move bestMove = null;
		S bestScore = null;
		while (moves.hasNext()) {
			var move = moves.next();
			S score = search(position, depth, player.opponent()).betterOf(bestScore, player);
			if (score != bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		return Optional.ofNullable(bestMove);
	}

	S search(P position, int depth, Player player) throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		if (position.isTerminal()) {
			var score = evaluator.evaluate(position);
			assert score.isTerminal();
			return score;
		}
		if (--depth < 0)
			return evaluator.evaluate(position);
		var moves = moveGenerator.generateMoves(position, player.opponent()).iterator();
		if (!moves.hasNext())
			return evaluator.evaluateDraw(position);
		S result = null;
		do {
			result = search(position.afterMove(moves.next()), depth, player.opponent()).betterOf(result, player);
		} while (moves.hasNext());
		return result;
	}

}
