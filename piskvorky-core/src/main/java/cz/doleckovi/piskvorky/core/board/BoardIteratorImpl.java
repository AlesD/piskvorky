package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;

import java.util.ListIterator;
import java.util.PrimitiveIterator;

/** Iterator for navigation over field indexes of board. */
class BoardIteratorImpl implements PrimitiveIterator.OfInt, ListIterator<Integer> {

	private final int start;
	private final int end;
	private final int step;
	private int nextIndex;
	private int previousIndex;

	BoardIteratorImpl(BoardImpl board, int index, Direction direction) {
		int length;
		int offset;
		if (direction == Direction.LEFT) {
			length = board.getWidth();
			step = 1;
			offset = index % board.getWidth();
		} else {
			int row = index / board.getWidth();
			if (direction == Direction.DOWN) {
				length = board.getHeight();
				step = board.getWidth();
				offset = row;
			} else {
				int column;
				if (direction == Direction.DOWN_RIGHT) {
					column = index % board.getWidth();
					step = board.getWidth() + 1;
				} else {
					column = board.getWidth() - 1 - index % board.getWidth();
					step = board.getWidth() - 1;
				}
				int diagonal = row - column;
				if (diagonal < 0) {
					length = Integer.min(board.getHeight(), board.getWidth() + diagonal);
					offset = row;
				} else {
					length = Integer.min(board.getWidth(), board.getHeight() - diagonal);
					offset = column;
				}
			}
		}
		start = index - (offset * step);
		end = start + (length - 1) * step;
		nextIndex = index + step;
		previousIndex = index;
	}

	private BoardIteratorImpl(BoardIteratorImpl iterator) {
		this.start = iterator.end;
		this.end = iterator.start;
		this.step = - iterator.step;
		this.previousIndex = iterator.previousIndex;
		this.nextIndex = previousIndex + step;

	}

	@Override
	public boolean hasNext() {
		return previousIndex != end;
	}

	@Override
	public int nextInt() {
		previousIndex = nextIndex;
		nextIndex += step;
		return previousIndex;
	}

	@Override
	public Integer next() {
		return PrimitiveIterator.OfInt.super.next();
	}

	@Override
	public boolean hasPrevious() {
		return nextIndex != start;
	}

	@Override
	public Integer previous() {
		nextIndex = previousIndex;
		previousIndex -= step;
		return nextIndex;
	}

	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public int previousIndex() {
		return previousIndex;
	}

	/** Board iterator in reverse direction.
	 * @return Board iterator with same <em>previous</em> index but advancing in opposite direction
	 */
	public BoardIteratorImpl reverseIterator() {
		return new BoardIteratorImpl(this);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Integer integer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(Integer integer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BoardIteratorImpl that = (BoardIteratorImpl) o;

		if (start != that.start) return false;
		if (end != that.end) return false;
		return nextIndex == that.nextIndex;
	}

	@Override
	public int hashCode() {
		int result = nextIndex;
		result = 31 * result + start;
		result = 31 * result + end;
		return result;
	}

	@Override
	public String toString() {
		return "BoardIteratorImpl[" + start + '>' + nextIndex + '>' + end + ']';
	}

}
