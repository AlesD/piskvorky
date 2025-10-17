package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.api.search.Move;
import cz.doleckovi.piskvorky.api.search.Search;

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
	 * <p>This method is used by {@link Game}.</p>
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
	 * <p>This method is used by {@link Search} algorithm. Default implementation delegates to {@link #withStone}.
	 * The positions might override this method to employ caching or use information provided by move implementations.</p>
	 * @param move Move to execute
	 * @return New position after move execution
	 * @throws IndexOutOfBoundsException if either move column or line does not fit to board
	 * @throws IllegalArgumentException  if the move can't be executed
	 * @throws IllegalStateException     if executed against terminal position
	 */
	default T afterMove(Move move)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		return withStone(move.column(), move.row(), move.side().stone);
	}

	/** Gets stone on specific place on board.
	 * @param column Board column
	 * @param row Board line
	 * @return Stone on the given position
	 * @throws IndexOutOfBoundsException if either column or line does not fit to board
	 */
	Stone stone(int column, int row) throws IndexOutOfBoundsException;

}
