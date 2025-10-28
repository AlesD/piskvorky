package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.position.Cell;
import cz.doleckovi.piskvorky.api.position.Line;
import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SimpleLine<C extends Cell> implements Constants, Line<C> {

	private static void adjustCells(SimpleCell cell, ListIterator<SimpleCell> cells) {
		while (cells.hasNext()) {
			var cellAfter = cells.next();
			if (cellAfter.stone() == Stone.EMPTY) {
				if (cells.hasNext()) {
					var next = cells.next();
					// TODO Maybe use own version of iterator that would allow something like "setPrevious"
					cells.previous();
					cells.set(new SimpleCell(Stone.EMPTY, cell.white() + next.white(), next.black() + next.black()));
				} else {
					cells.set(new SimpleCell(Stone.EMPTY, cell.white(), cell.black()));
				}
				break;
			}
			if (cellAfter.stone() != cell.stone())
				break;
			cells.set(cell);
		}
	}

	private final List<C> cells;
	private final boolean terminal;

	private SimpleLine(List<C> cells, boolean terminal) {
		this.cells = List.copyOf(cells);
		this.terminal = terminal;
	}

	public SimpleLine(int length) {
		this(List.of(SimpleCell.arrayOf(length)), false);
	}

	@Override
	public C cell(int index) {
		return cells.get(index);
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	/** Places stone on the line.
	 * @param offset Offset from the beginning of line
	 * @param stone Stone to place
	 * @return New line instance with the stone at given offset
	 * @throws IllegalStateException if line is terminal or if cell at given offset is not empty
	 * @throws IndexOutOfBoundsException if the offset is less than zero or greater or equal to line length
	 * @throws IllegalArgumentException if the stone is {@link Stone#EMPTY}
	 */
	@Override
	public SimpleLine withStone(int offset, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		if (terminal)
			throw new IllegalStateException("Line is terminal");
		if (stone == Stone.EMPTY)
			throw new IllegalArgumentException("Stone is EMPTY");
		var cell = cells.get(offset).withStone(stone);
		var cells = new ArrayList<>(this.cells);
		cells.set(offset, cell);
		adjustCells(cell, cells.listIterator());
		adjustCells(cell, cells.reversed().listIterator());
		var terminal = switch (stone) {
			case WHITE -> cell.white() >= SIZE;
			case BLACK -> cell.black() >= SIZE;
			default -> false;
		};
		return new SimpleLine(cells, terminal);
	}

}
