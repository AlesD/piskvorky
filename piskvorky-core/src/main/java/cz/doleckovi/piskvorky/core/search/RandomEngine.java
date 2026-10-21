package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Position;
import cz.doleckovi.piskvorky.api.search.Engine;
import cz.doleckovi.piskvorky.api.search.MoveGenerator;

import java.util.Optional;
import java.util.random.RandomGenerator;

public class RandomEngine implements Engine {

	private final MoveGenerator moveGenerator;
	private final RandomGenerator randomGenerator;

	public RandomEngine(MoveGenerator moveGenerator, RandomGenerator randomGenerator) {
		this.moveGenerator = moveGenerator;
		this.randomGenerator = randomGenerator;
	}

	public RandomEngine(MoveGenerator moveGenerator) {
		this(moveGenerator, RandomGenerator.getDefault());
	}

	public RandomEngine() {
		this(LegalMoveGenerator.INSTANCE, RandomGenerator.getDefault());
	}

	@Override
	public Optional<FieldAddress> bestMove(Position position, Side side) {
		if (position.isTerminal())
			throw new IllegalStateException("Can't generate move from terminal position");
		var moves = moveGenerator.generateMoves(position, side);
		if (moves.isEmpty())
			return Optional.empty();
		return Optional.of(moves.get(randomGenerator.nextInt(moves.size())));
	}

}
