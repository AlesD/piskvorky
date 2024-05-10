package cz.doleckovi.piskvorky.core.evaluator;

public enum StoneValue {

    /** Blocked stone that does not have enough space around to become part of
    /** Single stone, that has enough empty fields around that still allows it to become pattern of 5 stones. */
    ONE,
    /** Stone that belongs to pattern of three stones, but is separated by 2 or 3 empty fields. */
    SPLIT_TWO,
    TWO,
    ADJACENT_TWO,
    /** Stone that belongs to pattern of three stones, but is separated by 2 empty fields. */
    SPLIT_THREE,
    /** Stone that belongs to pattern of three stones. */
    THREE,
    /** Stone that belongs to pattern of three stones, and is adjacent to other stone. */
    ADJACENT_THREE,
    FOUR,
    ADJACENT_FOUR,
    FIVE;

}
