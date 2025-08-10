package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

public class PositionImpl implements Position<PositionImpl> {

	private class LineIterator implements Iterator<Line> {
		private int nextIndex = 0;

		@Override
		public boolean hasNext() {
			return nextIndex < lines.length;
		}

		@Override
		public Line next() throws NoSuchElementException {
			if (nextIndex >= lines.length)
				throw new NoSuchElementException("No more lines");
			return lines[nextIndex++];
		}
	}

	final Field[][] fields;
	final LineImpl[] lines;

	public final BoardImpl board;
	public final boolean terminal;

	PositionImpl(BoardImpl board, Field[][] fields, LineImpl[] lines, boolean terminal) {
		this.board = board;
		this.fields = fields;
		this.lines = lines;
		this.terminal = terminal;
	}

	@Override
	public BoardImpl board() {
		return board;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public Stone stone(int column, int row) throws IndexOutOfBoundsException {
		return fields[row][column].stone();
	}

	@Override
	public PositionImpl withStone(int column, int row, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		if (terminal)
			throw new IllegalStateException("Position is in terminal state");
		if (column < 0 || column >= board.width())
			throw new IndexOutOfBoundsException("Column %s is out of board");
		if (row < 0 || row >= board.height())
			throw new IndexOutOfBoundsException("Row %s is out of board");
		if (stone(column, row) != Stone.EMPTY)
			throw new IllegalArgumentException(String.format("There is already stone on [%s, %s]", column, row));
		if (stone == Stone.EMPTY)
			return this;
		var newFields = fields.clone();
		var newLines = lines.clone();
		var newTerminal = false;
		var newRow = newFields[row].clone();
		newRow[column] = newRow[column].withStone(stone);
		newFields[row] = newRow;
		for (var lineAddress : board.lineAddresses(column, row)) {
			var lineIndex = lineAddress.lineIndex();
			var newLine = lines[lineIndex].withStone(lineAddress.offset(), stone);
			newLines[lineIndex] = newLine;
			newTerminal = newTerminal || newLine.isTerminal();
		}
		return new PositionImpl(board, newFields, newLines, newTerminal);
	}

	public Iterator<Line> lines() {
		return new LineIterator();
	}

	@Override
	public String toString() {
		var result = new StringJoiner(System.lineSeparator());
		for (var row: fields)
			result.add(Arrays.toString(row));
		return result.toString();
	}
}