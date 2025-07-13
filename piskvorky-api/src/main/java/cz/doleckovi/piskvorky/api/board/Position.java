package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.search.Move;

/** Position on board.
 * <p>Position must be thread-safe and immutable.</p>
 */
public interface Position<T extends Position<T>> {

	/** Gets board on which is the game played.
	 * @return Board instance
	 */
	Board<T> board();

	/** Determine if the position is terminal. */
	boolean isTerminal();

	/** Creates new position with given stone.
	 * @param column Board column
	 * @param row Board line
	 * @param stone Stone to place
	 * @return New position with the stone on it
	 * @throws IndexOutOfBoundsException if either column or line does not fit to board
	 * @throws IllegalArgumentException  if there is already stone on the row and column
	 * @throws IllegalStateException     if invoked on terminal position
	 */
	T withStone(int column, int row, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

	/** Creates new position by executing move.
	 * @param move Move to execute
	 * @return New position after move execution
	 * @throws IndexOutOfBoundsException if either move column or line does not fit to board
	 * @throws IllegalArgumentException  if the move can't be executed
	 * @throws IllegalStateException     if executed against terminal position
	 */
	default T afterMove(Move move)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		return withStone(move.column(), move.row(), move.player().stone);
	}

	/** Gets stone on specific place on board.
	 * @param column Board column
	 * @param row Board line
	 * @return Stone on the given position
	 * @throws IndexOutOfBoundsException if either column or line does not fit to board
	 */
	Stone stone(int column, int row) throws IndexOutOfBoundsException;

}
