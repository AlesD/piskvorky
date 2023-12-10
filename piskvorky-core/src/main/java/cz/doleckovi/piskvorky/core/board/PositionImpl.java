package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.Player;
import cz.doleckovi.piskvorky.api.board.Field;
import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class PositionImpl implements Position {

	/** Constructs new instance.
	 * @param board Uninitialized board
	 * @return Fully initialized position without any stones
	 */
	static PositionImpl emptyPosition(BoardImpl board) {
		var fields = new FieldImpl[board.getWidth() * board.getHeight()];
		Arrays.fill(fields, new FieldImpl(Stone.EMPTY, 0));
		return new PositionImpl(board, false, 0, fields);
	}

	private class FieldList extends AbstractList<Field> {

		@Override
		public Field get(int index) {
			return fields[index];
		}

		@Override
		public int size() {
			return fields.length;
		}

	}

	private final BoardImpl board;
	private final boolean terminal;
	private final int generation;
	private final FieldImpl[] fields;
	private final List<Field> fieldList;

	private PositionImpl(BoardImpl board, boolean terminal, int generation, FieldImpl[] fields) {
		this.board = board;
		this.terminal = terminal;
		this.generation = generation;
		this.fields = fields;
		fieldList = new FieldList();
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public List<Field> fields() {
		return fieldList;
	}

	@Override
	public Position setStone(int index, Player player)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		if (terminal)
			throw new IllegalStateException("Can't place stone in terminal position");
		if (fields[index].stone != Stone.EMPTY)
			throw new IllegalArgumentException("Field is not empty");
		var fields = Arrays.copyOf(this.fields, this.fields.length);
		var newGeneration = generation + 1;
		fields[index] = new FieldImpl(player.stone, newGeneration);
		for (var dir : Direction.values()) {
			var iterator = board.iterator(index, dir);
			var reverseIterator = iterator.reverseIterator();
			var stonesInRow = 1;
			while (iterator.hasNext() && fields[iterator.nextInt()].stone == player.stone) {
				if (++stonesInRow == 5)
					return new PositionImpl(board, true, newGeneration, fields);
			}
			while (reverseIterator.hasNext() && fields[reverseIterator.nextInt()].stone == player.stone) {
				if (++stonesInRow == 5)
					return new PositionImpl(board, true, newGeneration, fields);
			}
		}
		return new PositionImpl(board, false, newGeneration, fields);
	}

}
