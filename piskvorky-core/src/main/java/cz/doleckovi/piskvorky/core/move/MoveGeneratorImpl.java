package cz.doleckovi.piskvorky.core.move;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.api.move.Move;
import cz.doleckovi.piskvorky.api.move.MoveGenerator;

import java.util.ArrayList;
import java.util.List;

public class MoveGeneratorImpl implements MoveGenerator {

	@Override
	public List<Move> generateMoves(Position position) {
		var result = new ArrayList<Move>();
		for (var row = position.getBoard().getHeight() - 1; row >= 0; --row)
			for (var column = position.getBoard().getWidth() - 1; column >= 0; --column)
				if (position.stone(column, row) == Stone.EMPTY)
					result.add(new MoveImpl())


		return List.of();
	}

}
