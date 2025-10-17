package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.Arrays;

public class SimplePosition implements Constants, Position<SimplePosition> {

	private final Board<SimplePosition> board;
	private final boolean terminal;
	private final Stone[][] stones;

	SimplePosition(Board<SimplePosition> board) {
		var stones = new Stone[board.height()][];
		Arrays.fill(stones, new Stone[board.width()]);
		Arrays.fill(stones[0], Stone.EMPTY);
		this(board, false, stones);
	}

	private SimplePosition(Board<SimplePosition> board, boolean terminal, Stone[][] stones) {
		this.board = board;
		this.terminal = terminal;
		this.stones = stones;
	}

	@Override
	public Board<SimplePosition> board() {
		return board;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public SimplePosition withStone(int column, int row, Stone stone) throws IndexOutOfBoundsException,
			IllegalArgumentException, IllegalStateException
	{
		if (terminal)
			throw new IllegalStateException("Can't place stone on terminal position");
		if (stones[row][column] != Stone.EMPTY)
			throw new IllegalArgumentException("Can't place on non-empty field [%s, %s] %s".formatted(column, row, stones[column][row]));
		var stones = this.stones.clone();
		var line = stones[row].clone();
		line[column] = stone;
		stones[row] = line;
		for (var dir : Direction.values()) {
			int stonesInRow = 1;
			var x = column + dir.columnDelta;
			var y = row + dir.rowDelta;
			while (x >= 0 && y >= 0 && x < board.width() && y < board().height() && stones[y][x] == stone) {
				if (++stonesInRow == SIZE)
					return new SimplePosition(board, true, stones);
				x += dir.columnDelta;
				y += dir.rowDelta;
			}
			x = column - dir.columnDelta;
			y = row - dir.rowDelta;
			while (x >= 0 && y >= 0 && x < board.width() && y < board().height() && stones[y][x] == stone) {
				if (++stonesInRow == SIZE)
					return new SimplePosition(board, true, stones);
				x -= dir.columnDelta;
				y -= dir.rowDelta;
			}
		}
		return new SimplePosition(board, false, stones);
	}

	@Override
	public Stone stone(int column, int row) throws IndexOutOfBoundsException {
		return stones[row][column];
	}

}
