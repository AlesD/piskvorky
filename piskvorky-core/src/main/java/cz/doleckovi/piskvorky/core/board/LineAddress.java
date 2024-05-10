package cz.doleckovi.piskvorky.core.board;

public record LineAddress(
		int lineIndex,
		int offset,
		FieldAddress fieldAddress)
{ }
