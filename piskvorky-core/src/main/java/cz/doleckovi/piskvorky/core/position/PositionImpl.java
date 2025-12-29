package cz.doleckovi.piskvorky.core.position;

import cz.doleckovi.piskvorky.api.board.*;
import cz.doleckovi.piskvorky.api.position.Position;
import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class PositionImpl implements Position {

    private final Board board;
    private final Stone[] stones;
	private final PositionData data;
	private final LineData[] lines;
	private final FieldData[] fields;
    private final CellData[][] cells;

    public PositionImpl(Board board) {
        this.board = board;
        stones = new Stone[board.fieldCount()];
        Arrays.fill(stones, Stone.EMPTY);
        data = PositionData.EMPTY;
        lines = new LineData[board.lineCount()];
        Arrays.fill(lines, LineData.EMPTY);
        fields = new FieldData[board.fieldCount()];
        Arrays.fill(fields, FieldData.EMPTY);
        cells = new CellData[board.lineCount()][];
        var cache = new CellData[board.size() + 1][];
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

    private PositionImpl(PositionImpl position, FieldAddress field, Stone stone) {
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

    private LineData updateLine(CellAddress cell, Stone stone, FieldData.Updater field)
        throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
    {
        var cells = this.cells[cell.line()] = this.cells[cell.line()].clone();
        var line = lines[cell.line()].updater();
        var oldData = cells[cell.offset()];
        var newData = cells[cell.offset()] = oldData.withStone(stone);
        field.update(oldData, newData);
        line.update(oldData, newData);
        var descriptor = board.line(cell.line());
        adjustCells(descriptor, cells, newData, cell.offset(), cells.length, 1, line);
        adjustCells(descriptor, cells, newData, cell.offset(), -1, -1, line);
        return line.newLine();
    }

    /** Adjust cells with same stone.
     * @param descriptor Line descriptor
     * @param cells Line cells data
     * @param newData New data to set
     * @param offset Offset of last changed cell
     * @param end Offset after last cell of line
     * @param step Offset step
     * @param line Line updater to notify about cell data changes
     */
    private void adjustCells(final LineDescriptor descriptor, final CellData[] cells,
            CellData newData, int offset, int end, int step, LineData.Updater line)
    {
        while ((offset += step) != end) {
            CellData oldData = cells[offset];
            if (oldData.stone() == newData.stone()) {
                // Cell with same stone => will have same data as last changed
                cells[offset] = newData;
                // Update filed data
                var fieldIndex = descriptor.field(offset).index();
                fields[fieldIndex] = fields[fieldIndex].withCell(oldData, newData);
                // Notify line about data change
                line.update(oldData, newData);
            } else if (oldData.stone() != Stone.EMPTY) {
                return;
            } else {
                // Empty cell => needs to recalculate data according to last changed and next
                adjustEmptyCells(descriptor, cells, oldData, newData, offset, end, step, line);
                return;
            }
        }
    }

    /** Adjust empty cells next to changed cell with stone.
     * @param descriptor Line descriptor
     * @param cells Line cells data
     * @param oldData
     * @param previous
     * @param offset Offset of empty cell to adjust
     * @param end Offset after last cell of line
     * @param step Offset step
     * @param line Line updater to notify about cell data changes
     */
    private void adjustEmptyCells(LineDescriptor descriptor, CellData[] cells,
                          CellData oldData, CellData previous, int offset, int end, int step, LineData.Updater line)
    {
        var field = descriptor.field(offset);
        var nextOffset = offset + step;
        CellData next;
        if (nextOffset == end) {
            // There is no next
            next = null;
            previous = oldData.nextTo(previous);
        } else {
            next = cells[nextOffset];
            previous = oldData.between(previous, next);
        }
        cells[offset] = previous;
        fields[field.index()] = fields[field.index()].withCell(oldData, previous);
        line.update(oldData, previous);
        if (next != null) while (next.stone() == Stone.EMPTY) {
            // If the next cell is empty it might become move candidate
            previous = next.asMoveCandidate(previous);
            if (previous == next) return; // No change
            cells[nextOffset] = previous;
            field = descriptor.field(nextOffset);
            fields[field.index()] = fields[field.index()].withCell(next, previous);
            line.update(next, previous);
            if ((nextOffset += step) == end) return; // No next
            next = cells[nextOffset];
        }
    }

    @Override
    public Board board() {
        return board;
    }

    @Override
    public Stone stone(FieldAddress field) {
        return stones[field.index()];
    }

    @Override
    public boolean isTerminal() {
        return data.isTerminal();
    }

    @Override
	public PositionImpl withStone(FieldAddress fieldAddress, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
        if (stones[fieldAddress.index()] != Stone.EMPTY)
            throw new IllegalStateException("Field %s is not empty".formatted(fieldAddress));
        return new PositionImpl(this, fieldAddress, stone);
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
