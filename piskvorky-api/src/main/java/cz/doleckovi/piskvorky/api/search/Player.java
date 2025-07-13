package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Stone;

/** Symbolic constants for players. */
public enum Player {

	/** Player playing with white stones. */
	WHITE(Stone.WHITE),
	/** Player playing with black stones. */
	BLACK(Stone.BLACK);

	/** Stone used by the player. */
	public final Stone stone;

	Player(Stone stone) {
		this.stone = stone;
	}

	/** Gets opponent of this player. */
	public Player opponent() {
		return switch (this) {
			case WHITE -> BLACK;
			case BLACK -> WHITE;
		};
	}

}
