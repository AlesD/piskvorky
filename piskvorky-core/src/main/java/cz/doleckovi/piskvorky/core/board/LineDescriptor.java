package cz.doleckovi.piskvorky.core.board;

public record LineDescriptor(
		int lineIndex,
		int length,
		Direction direction
) { }
