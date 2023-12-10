package cz.doleckovi.piskvorky.api.board;

/** Stone placed on board. */
public enum Stone {

	/** No stone. */
	EMPTY,
	/** White stone. */
	WHITE,
	/** Black stone. */
	BLACK;

	private static final char EMPTY_SYMBOL = '·';
	private static final char WHITE_SYMBOL = '⚪';
	private static final char BLACK_SYMBOL = '⚫';

	public static Stone valueOf(char symbol) {
		return switch (symbol) {
			case EMPTY_SYMBOL, '-', '?' -> EMPTY;
			case WHITE_SYMBOL, 'O', 'o' -> WHITE;
			case BLACK_SYMBOL, 'X', 'x' -> BLACK;
			default -> throw new IllegalArgumentException("Unknown stone symbol: " + symbol);
		};
	}

	@Override
	public String toString() {
		return switch (this) {
			case EMPTY -> Character.toString(EMPTY_SYMBOL);
			case WHITE -> Character.toString(WHITE_SYMBOL);
			case BLACK -> Character.toString(BLACK_SYMBOL);
		};
	}

}
