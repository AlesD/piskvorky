package cz.doleckovi.piskvorky.api.position;

/** Stone placed on board. */
public enum Stone {

	/** No stone.
     * <p>Artificial value meaning <em>no stone</em>. This stone cannot be put on board.</p>
     */
	EMPTY,
	/** White stone. */
	WHITE,
	/** Black stone. */
	BLACK,
    /** Blocking stone.
     * <p>Artificial value meaning <em>stone of neither side</em>. Used for testing and to create board with "holes".</p>
     */
	BLOCK;

	public static Stone valueOf(char symbol) {
		return switch (symbol) {
			case '·', ' ', '-' -> EMPTY;
			case '○', '⚪', 'O', 'o', 'W', 'w' -> WHITE;
			case '●', '⚫', 'X', 'x', 'B', 'b' -> BLACK;
			case '×', '#' -> BLOCK;
			default -> throw new IllegalArgumentException("Unknown stone symbol: " + symbol);
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
