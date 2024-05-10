package cz.doleckovi.piskvorky.api.ui;

import cz.doleckovi.piskvorky.api.board.Player;
import cz.doleckovi.piskvorky.api.traits.IdTrait;

/** Information about {@link Player}.*/
public interface PlayerInfo extends IdTrait<String> {

	/** Gets player name. */
	String getName();

}
