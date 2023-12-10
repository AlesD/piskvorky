package cz.doleckovi.piskvorky.api.board;

/** Symbolic constants for players. */
public enum Player {

	/** Player playing with white stones. */
	WHITE(Stone.WHITE),
	/** Player playing with black stones. */
	BLACK(Stone.BLACK);

	/** Stone used by the player.
	 * <p>The value is never {@link Stone#EMPTY}.</p>
	 */
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
