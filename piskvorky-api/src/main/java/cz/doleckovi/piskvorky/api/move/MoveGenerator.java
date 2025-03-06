package cz.doleckovi.piskvorky.api.move;

import cz.doleckovi.piskvorky.api.board.Position;

import java.util.List;

public interface MoveGenerator {

	List<Move> generateMoves(Position position);

}
