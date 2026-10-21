package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Position;
import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.Optional;

@FunctionalInterface
public interface Engine {

	Optional<FieldAddress> bestMove(Position position, Side side);

}
