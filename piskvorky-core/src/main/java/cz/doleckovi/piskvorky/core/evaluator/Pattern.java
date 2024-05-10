package cz.doleckovi.piskvorky.core.evaluator;

public enum Pattern {

	NONE("0"),
	ONE("1"),
	TWO("2"),
	ADJACENT_TWO("2\u032E"),
	SPLIT_THREE("3\u032A"),
	THREE("3"),
	OPEN_THREE("3"),
	OPEN_ADJACENT_THREE("3\u032E"),
	FOUR("4"),
	ADJACENT_OPEN_FOUR("4\u032E"),
	FIVE_IN_ROW("5");

    /** Number of player stones that make pattern. */
    public static final int LENGTH = 5;
    /** Number of all possible patterns.
     * <p>Number of combinations of stones ×2 - once if extra opponent stone is empty and once if it present.</p>
     */
    public static final int COUNT = (1 << LENGTH) * 2;
    /** Mask for extra <strong>opponent</strong> stone at start. */
    public static final int OPPONENT_MASK = 1 << LENGTH;
    /** Mask for player stones. */
    public static final int PLAYER_MASK = OPPONENT_MASK - 1;

    private final String string;

	Pattern(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

}
