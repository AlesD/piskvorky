package cz.doleckovi.piskvorky.core.board;

import java.util.Map;

public record FieldDescriptor(
		FieldAddress fieldAddress,
		Map<Direction, LineAddress> lineAddresses)
{ }
