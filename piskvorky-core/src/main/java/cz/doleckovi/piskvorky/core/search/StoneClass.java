package cz.doleckovi.piskvorky.core.search;

public enum StoneClass {

	/** Stone that can't become part of 5-in-row.
	 * <p>#--O-X--#</p>
	 */
	NONE(0, true),

	/** Standalone stone. */
	ONE(1, true),

	/** One of 2 stone separated by 2 or 3 empty fields. */
	SPLIT_TWO(2, true),

	/** One of 2 stones separated by 1 empty field. */
	TWO(2, true),

	/** One of 2 adjacent stones. */
	ADJACENT_TWO(2, true),

	/** One of 3 stones that can't become open four by adding another stone.
	 * <p>Opponent does not have to attend such three stones immediately. The three stones are either split by 2 empty
	 * fields (O-O-O or O--OO) or blocked.</p>
	 */
	THREE(3, true), // 3 + Combining Bridge Below (U+032E)

	/** One of 3 stones that can become open four by adding another stone.
	 * <p>Opponent has to attend such three stones unless it can make four.</p>
	 */
	OPEN_THREE(3, false),

	/** One of 4 stones which are either separated by 1 empty field or blocked on 1 side. */
	FOUR(4, false),

	/** One of 4 adjacent stones with empty field on both sides. */
	OPEN_FOUR(4, false),

	/** One of 5 adjacent stones. */
	FIVE(5, false);

	private final int cardinality;
	private final boolean stable;

	StoneClass(int cardinality, boolean stable) {
		this.cardinality = cardinality;
		this.stable = stable;
	}

	/** Class cardinality.
	 * @return Number of stones in pattern to which the stone belongs
	 */
	public int cardinality() {
		return cardinality;
	}

	/** Stability flag.
	 * @return {@code false} if position with this stone is terminal, or if its evaluation is very likely to change
	 */
	public boolean stable() {
		return stable;
	}

	@Override
	public String toString() {
		return switch (this) {
			case NONE -> "0";
			case ONE -> "1";
			case SPLIT_TWO -> "2-";
			case TWO -> "2";
			case ADJACENT_TWO -> "2+";
			case THREE -> "3";
			case OPEN_THREE -> "3+";
			case FOUR -> "4";
			case OPEN_FOUR -> "4+";
			case FIVE -> "5";
		};
	}
}
