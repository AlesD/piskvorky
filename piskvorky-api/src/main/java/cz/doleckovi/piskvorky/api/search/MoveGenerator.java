package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Position;

import java.util.List;

@FunctionalInterface
public interface MoveGenerator {

	List<FieldAddress> generateMoves(Position position, Side side);

}
