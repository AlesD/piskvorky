package cz.doleckovi.piskvorky.core.search;

public enum StoneClass {

	/** Stone that can't become part of 5-in-row. */
	NONE(0, true, "0"),
	/** Standalone stone. */
	ONE(1, true, "1"),
	/** One of 2 stone separated by 2 or 3 empty fields. */
	SPLIT_TWO(2, true, "2̪"), // 2 + Combining Bridge Below (U+032E)),
	/** One of 2 stones separated by 1 empty field. */
	TWO(2, true, "2"),
	/** One of 2 adjacent stones. */
	ADJACENT_TWO(2, true, "2̮"), // 2 + Combining Breve Below (U+032E)
	/** One of 3 stones separated by 2 empty fields. */
	SPLIT_THREE(3, true, "3̪"), // 3 + Combining Bridge Below (U+032E)
	/** One of 3 stones separated by 1 empty field or unable to become adjacent open four. */
	THREE(3, true, "3"),
	/** One of 3 stones separated by 1 empty field with empty field on both sides. */
	OPEN_THREE(3, false, "3"),
	/** One of 3 adjacent stones - with empty field on one side and 2 empty fields on other side. */
	OPEN_ADJACENT_THREE(3, false, "3̮"), // 3 + Combining Breve Below (U+032E)
	/** One of 4 stones which are either separated by 1 empty field or adjacent to opponent stone. */
	FOUR(4, false, "4"),
	/** One of 4 adjacent stones with empty field on both sides. */
	ADJACENT_OPEN_FOUR(4, false, "4̮"), // 4 + Combining Breve Below (U+032E)
	/** One of 5 adjacent stones. */
	FIVE_IN_ROW(5, false, "5");

	private final int cardinality;
	private final boolean stable;
	private final String symbol;

	/**
	 * @param cardinality Number of stones in the pattern
	 * @param stable Is position with this stone considered stable
	 * @param symbol Short representation of the class
	 */
	StoneClass(int cardinality, boolean stable, String symbol) {
		this.cardinality = cardinality;
		this.stable = stable;
		this.symbol = symbol;
	}

	public int cardinality() {
		return cardinality;
	}

	public boolean stable() {
		return stable;
	}

	public String symbol() {
		return symbol;
	}
}
