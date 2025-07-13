package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Position;

import java.util.Collection;

public interface MoveGenerator<P extends Position<P>> {

	Collection<Move> generateMoves(P position, Player player);

	Collection<Move> generateKillerMoves(P position, Player player);

}
