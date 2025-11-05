package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.FieldAddress;

import java.util.Map;

public class FieldAddressImpl implements FieldAddress {

    private final int index;
    private final int column;
    private final int row;
    private final Map<Direction, CellAddressImpl> cells;

    FieldAddressImpl(int index, int column, int row, Map<Direction, CellAddressImpl> cells) {
        this.index = index;
        this.column = column;
        this.row = row;
        this.cells = Map.copyOf(cells);
    }

    public int index() {
        return index;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public int row() {
        return row;
    }

    @Override
    public CellAddressImpl cell(Direction direction) {
		return cells.get(direction);
	}

}
