package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.evaluation.Score;

public interface TranspositionTable<P extends Position<P>, S extends Score<S>> {

	TranspositionTableEntry<S> lookup(P position, int depth);

	void store(P position, int depth, S score, EntryType type);

}

