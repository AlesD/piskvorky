package cz.doleckovi.piskvorky.core.search;

/** Type of transposition table entry. */
public enum EntryType {
	/** Exact score for given position and search depth. */
	EXACT,
	/** Minimal score for given position and search depth. */
	LOWER_BOUND,
	/** Maximal score for given position and search depth. */
	UPPER_BOUND
}
