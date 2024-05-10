package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;

public record LineDescriptor(
		int lineIndex,
		int length,
		Direction direction
) { }
