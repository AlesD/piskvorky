package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.evaluation.Score;

public record TranspositionTableEntry<S extends Score<S>>(S score, EntryType type) {}
