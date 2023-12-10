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
	 * @return true if the position contains 5-in-row
	 */
	boolean isTerminal();

	/** Fields of the board.
	 * @return Unmodifiable list of fields
	 */
	List<Field> fields();

	/** Puts stone on specific place on board.
	 * @param index Field index
	 * @param player Player placing the stone on board
	 * @return New position with stone on specified field
	 * @throws IndexOutOfBoundsException if the index does not fit to board
	 * @throws IllegalArgumentException  if the there is already stone on given row or column
	 * @throws IllegalStateException     if invoked on terminal position
	 */
	Position setStone(int index, Player player)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

}
