package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.search.Score;

public class NoOpTranspositionTable<P extends Position<P>, S extends Score<S>> implements TranspositionTable<P, S> {

	@Override
	public TranspositionTableEntry<S> lookup(P position, int depth) {
		return null;
	}

	@Override
	public void store(P position, int depth, S score, EntryType type) {
		// Do nothing
	}

}
