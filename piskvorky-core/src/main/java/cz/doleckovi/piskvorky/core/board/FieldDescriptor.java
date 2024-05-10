package cz.doleckovi.piskvorky.core.board;

import java.util.Map;

import cz.doleckovi.piskvorky.api.board.Direction;

public record FieldDescriptor(
		FieldAddress fieldAddress,
		Map<Direction, LineAddress> lineAddresses)
{ }
