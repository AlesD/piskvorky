package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.Position;

public class BoardImpl implements Board {

	private final int width;
	private final int height;
	private final PositionImpl initialPosition;

	/** Create new board instance.
	 * @param width Board width
	 * @param height Boar height
	 */
	public BoardImpl(int width, int height) {
		if (width < 5)
			throw new IllegalArgumentException("Minimal width of the board is 5");
		if (height < 5) {
			throw new IllegalArgumentException("Minimal height of the board is 5");
		}
		this.width = width;
		this.height = height;
		this.initialPosition = PositionImpl.emptyPosition(this);
	}

	/** Create new square board instance.
	 * @param side Board side length
	 */
	public BoardImpl(int side) {
		this(side, side);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Position initialPosition() {
		return initialPosition;
	}

	@Override
	public int toIndex(int column, int row) throws IndexOutOfBoundsException {
		if (column < 0 || column >= width)
			throw new IndexOutOfBoundsException(column);
		if (row < 0 || row >= height)
			throw new IndexOutOfBoundsException(row);
		return column + row * width;
	}

	/** Create iterator over field indexes.
	 * @param index Initial field index
	 * @param direction Direction in which the iterator advances
	 * @return Iterator that will enumerate indexes of next fields in given direction
	 */
	BoardIteratorImpl iterator(int index, Direction direction) {
		return new BoardIteratorImpl(this, index, direction);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BoardImpl that = (BoardImpl) o;
		return width == that.width && height == that.height;
	}

	@Override
	public int hashCode() {
		return width + 31 * height;
	}

	@Override
	public String toString() {
		return "Board[" + width + 'x' + height + ']';
	}

}
