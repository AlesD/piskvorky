package cz.doleckovi.piskvorky.core.search;

public enum StoneValue {

    /** Stone that can't become part of 5-in-row. */
    BLOCK,
    /** Standalone stone. */
    ONE,
    /** Stone separated by 2 or 3 empty fields from other stone. */
    SPLIT_TWO,
    /** Stone separated by 1 empty field from other stone. */
    TWO,
    /** Stone adjacent to other stone. */
    ADJACENT_TWO,
    /** Stone separated by 2 empty fields from other 2 stones. */
    SPLIT_THREE,
    /** Stone separated by 1 empty fields from other 2 stones. */
    THREE,
    /** Stone adjacent to other 2 stones. */
    ADJACENT_THREE,
    /** Stone separated by 1 empty fields from other 3 stones. */
    FOUR,
    /** Stone separated by 1 empty fields from other 2 stones. */
    ADJACENT_FOUR,
    FIVE;

}
