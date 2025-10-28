package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.CellAddress;
import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.FieldAddress;

import java.util.Collection;
import java.util.Map;

class SimpleFieldAddress implements FieldAddress {

    private final int index;
    private final int column;
    private final int row;
    private final Map<Direction, SimpleCellAddress> cells;

    public SimpleFieldAddress(int index, int column, int row, Map<Direction, SimpleCellAddress> cells) {
        this.index = index;
        this.column = column;
        this.row = row;
        this.cells = Map.copyOf(cells);
    }

    @Override
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
    public SimpleCellAddress cell(Direction direction) {
		return cells.get(direction);
	}

    Collection<SimpleCellAddress> cells() {
        return cells.values();
    }

}
