package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Position;

public interface TranspositionTable {

	enum EntryType {LOWER_BOUND, UPPER_BOUND, EXACT}
	record TranspositionTableEntry(int depth, int value, EntryType entryType) {}

	TranspositionTableEntry lookup(Position position);
	void store(Position position, TranspositionTableEntry ttEntry);

}
