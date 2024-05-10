package cz.doleckovi.piskvorky.api.board;

import java.util.List;

/** Position on board.
 * <p>Position must be thread-safe and immutable.</p>
 */
public interface Position {

	/** Gets board on which is the game played.
	 * @return Board instance
	 */
	Board getBoard();

	/** Determine if the position is terminal.
	 * @return true if the position contains 5-in-line
	 */
	boolean isTerminal();

	/**
	 * Fields of the board.
	 * @return Unmodifiable list of fields
	 * @deprecated Forces field organization into list or array
	 */
	@Deprecated
	List<? extends Field> fields();

	/** Fields of single line of the board.
	 * @param index Initial field
	 * @param direction Direction
	 * @return Field iterator that has specified field as previous
	 */
	FieldIterator<? extends Field> iterator(int index, Direction direction);

	/** Puts stone on specific place on board.
	 * @param column Board column
	 * @param row Board line
	 * @param player Player placing the stone on board
	 * @return New position with stone on specified field
	 * @throws IndexOutOfBoundsException if either column or line does not fit to board
	 * @throws IllegalArgumentException  if the there is already stone on given line or column
	 * @throws IllegalStateException     if invoked on terminal position
	 */
	Position setStone(int column, int row, Player player)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

	/** Gets stone on specific place on board.
	 * @param column Board column
	 * @param row Board line
	 * @return Stone on the given position
	 * @throws IndexOutOfBoundsException if either column or line does not fit to board
	 */
	Stone getStone(int column, int row) throws IndexOutOfBoundsException;

}
