package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Move;
import cz.doleckovi.piskvorky.api.board.Player;
import cz.doleckovi.piskvorky.api.board.Position;

public interface Search {

	Move search(Position position, Player player, int depth);

}
