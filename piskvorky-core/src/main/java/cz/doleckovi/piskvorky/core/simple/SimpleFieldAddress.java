package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.CellAddress;
import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.FieldAddress;

import java.util.Map;

record SimpleFieldAddress(int size, int column, int row, Map<Direction, CellAddress> cells) implements FieldAddress {

	@Override public CellAddress cell(Direction direction) {
		return cells.get(direction);
	}

}
