package cz.doleckovi.piskvorky.api.board;

/** Stone placed on board. */
public enum Stone {

	/** No stone. */
	EMPTY,
	/** White stone. */
	WHITE,
	/** Black stone. */
	BLACK,
	/** Blocking stone. */
	BLOCK;

	public static Stone valueOf(char symbol) {
		return switch (symbol) {
			case '·', ' ', '-' -> EMPTY;
			case '○', '⚪', 'O', 'o' -> WHITE;
			case '●', '⚫', 'X', 'x' -> BLACK;
			case '×', '#' -> BLOCK;
			default -> throw new IllegalArgumentException("Unknown stone symbol: " + symbol);
		};
	}

	public boolean compatible(Stone other) {
		return switch (this) {
			case EMPTY -> other == WHITE || other == BLACK;
			case WHITE -> other == WHITE;
			case BLACK -> other == BLACK;
			case BLOCK -> false;
		};
	}

	@Override
	public String toString() {
		return switch (this) {
			case EMPTY -> "·";
			case WHITE -> "○"; // White circle U+25CB
			case BLACK -> "●"; // Black circle U+25CF
			case BLOCK -> "×";
		};
	}

}
