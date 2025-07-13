package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.search.Player;
import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.api.search.Move;
import cz.doleckovi.piskvorky.api.search.MoveGenerator;
import cz.doleckovi.piskvorky.core.board.MoveImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoveGeneratorImpl<P extends Position<P>> implements MoveGenerator<P> {

	@Override
	public List<Move> generateMoves(P position, Player player) {
		var board = position.board();
		var width = board.width();
		var height = board.height();
		var result = new ArrayList<Move>(width * height);
		for (var row = 0; row < height; ++row)
			for (var column = 0; column < width; ++column)
				if (position.stone(column, row) == Stone.EMPTY)
					result.add(new MoveImpl(player, column, row));
		return result;
	}

	@Override
	public Collection<Move> generateKillerMoves(P position, Player player) {
		return List.of();
	}

}
