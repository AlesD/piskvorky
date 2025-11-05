package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class PositionImpl implements Position<PositionImpl> {

    private final BoardImpl board;
    private final Stone[] stones;
	private final PositionData data;
	private final LineData[] lines;
	private final FieldData[] fields;
    private final CellData[][] cells;

    PositionImpl(BoardImpl board) {
        this.board = board;
        stones = new Stone[board().fieldCount()];
        Arrays.fill(stones, Stone.EMPTY);
        data = PositionData.EMPTY;
        lines = new LineData[board.lineCount()];
        Arrays.fill(lines, LineData.EMPTY);
        fields = new FieldData[board.fieldCount()];
        Arrays.fill(fields, FieldData.EMPTY);
        cells = new CellData[board.lineCount()][];
        var cache = new CellData[cells.length + 1][];
        for (var lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
            var descriptor = board.line(lineIndex);
            var line = cache[descriptor.length()];
            if (line == null) {
                line = new CellData[descriptor.length()];
                Arrays.fill(line, CellData.EMPTY);
                cache[descriptor.length()] = line;
            }
            cells[lineIndex] = line;
        }
    }

    private PositionImpl(PositionImpl position, FieldAddressImpl field, Stone stone) {
        this.board = position.board;
        this.stones = position.stones.clone();
        this.lines = position.lines.clone();
        this.fields = position.fields.clone();
        this.cells = position.cells.clone();
        stones[field.index()] = stone;
        var oldFieldData = fields[field.index()];
        var fieldUpdater = oldFieldData.updater(); // Updated for field on intersection
        var positionUpdater = position.data.updater();
        for (var direction : Direction.values()) {
            var cell = field.cell(direction);
            if (cell == null)
                continue;
            var oldLineData = lines[cell.line()];
            var newLineData = updateLine(cell, stone, fieldUpdater);
            lines[cell.line()] = newLineData;
            positionUpdater.update(oldLineData, newLineData);
        }
        var newFieldData = fieldUpdater.newData();
        fields[field.index()] = newFieldData;
        positionUpdater.fieldDataChanged(oldFieldData, newFieldData);
        this.data = positionUpdater.newData();
    }

    private LineData updateLine(CellAddressImpl cell, Stone stone, FieldData.Updater field)
        throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
    {
        var cells = this.cells[cell.line()] = this.cells[cell.line()].clone();
        var line = lines[cell.line()].updater();
        var oldData = cells[cell.offset()];
        var newData = cells[cell.offset()] = oldData.withStone(stone);
        field.update(oldData, newData);
        line.update(oldData, newData);
        var descriptor = board.line(cell);
        adjustCells(descriptor, cells, newData, cell.offset(), cells.length, 1, line);
        adjustCells(descriptor, cells, newData, cell.offset(), -1, -1, line);
        return line.newLine();
    }

    private void adjustCells(final LineDescriptorImpl descriptor, final CellData[] cells,
            CellData newData, int offset, int end, int step, LineData.Updater line)
    {
        while ((offset += step) != end) {
            CellData oldData = cells[offset];
            if (oldData.stone() == newData.stone()) {
                // Cell with same stone => will have same data as last changed
                var field = descriptor.field(offset);
                cells[offset] = newData;
                fields[field.index()] = fields[field.index()].withCell(oldData, newData);
                line.update(oldData, newData);
            } else if (oldData.stone() != Stone.EMPTY) {
                return;
            } else {
                // Empty cell => needs to recalculate data according to last changed and next
                var field = descriptor.field(offset);
                var nextOffset = offset + step;
                CellData nextData;
                if (nextOffset == end) {
                    // There is no next
                    nextData = null;
                    newData = oldData.nextTo(newData);
                } else {
                    nextData = cells[nextOffset];
                    newData = oldData.between(newData, nextData);
                }
                cells[offset] = newData;
                fields[field.index()] = fields[field.index()].withCell(oldData, newData);
                line.update(oldData, newData);
                if (nextData != null) while (nextData.stone() == Stone.EMPTY) {
                    // If the next cell is empty it might become move candidate
                    newData = nextData.asMoveCandidate(newData);
                    if (newData == nextData) return; // No change
                    cells[nextOffset] = newData;
                    field = descriptor.field(nextOffset);
                    fields[field.index()] = fields[field.index()].withCell(nextData, newData);
                    line.update(nextData, newData);
                    if ((nextOffset += step) == end) return; // No next
                    nextData = cells[nextOffset];
                }
                return;
            }
        }
    }

    @Override
    public BoardImpl board() {
        return board;
    }

    @Override
    public Stone stone(FieldAddress field) {
        if (!(field instanceof FieldAddressImpl fieldAddressImpl))
            throw new IllegalArgumentException("fieldAddress");
        return stones[fieldAddressImpl.index()];
    }

    @Override
    public boolean isTerminal() {
        return data.isTerminal();
    }

    @Override
	public PositionImpl withStone(FieldAddress fieldAddress, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		if (!(fieldAddress instanceof FieldAddressImpl fieldAddressImpl))
			throw new IllegalArgumentException("fieldAddress");
        if (stones[fieldAddressImpl.index()] != Stone.EMPTY)
            throw new IllegalStateException("Field %s is not empty".formatted(fieldAddress));
        return new PositionImpl(this, fieldAddressImpl, stone);
	}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PositionImpl position = (PositionImpl) o;
        return Objects.equals(board, position.board) && Objects.deepEquals(stones, position.stones);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, Arrays.hashCode(stones));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PositionImpl.class.getSimpleName() + "[", "]")
                .add("size=" + board.size())
                .add("stones=" + Arrays.toString(stones))
                .toString();
    }
}
