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

	/** Creates new position with given stone.
	 * @param column Board column
	 * @param row Board line
	 * @param stone Stone to place
	 * @return New position with the stone on it
	 * @throws IndexOutOfBoundsException if either column or line does not fit to board
	 * @throws IllegalArgumentException  if there is already stone other than EMPTY
	 * @throws IllegalStateException     if invoked on terminal line
	 */
	Position withStone(int column, int row, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

	/** Gets stone on specific place on board.
	 * @param column Board column
	 * @param row Board line
	 * @return Stone on the given position
	 * @throws IndexOutOfBoundsException if either column or line does not fit to board
	 */
	Stone stone(int column, int row) throws IndexOutOfBoundsException;

}
