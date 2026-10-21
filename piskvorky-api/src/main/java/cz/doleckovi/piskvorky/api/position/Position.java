package cz.doleckovi.piskvorky.api.position;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.FieldAddress;

/** Position on board.
 * <p>Position must be thread-safe.</p>
 */
public interface Position {

	/** Gets board on which is the game played.
	 * @return Board instance
	 */
	Board board(); // Does position really need board?

    /** Gets stone on given field.
     * @param field Field address
     * @return Stone on given field
     */
    Stone stone(FieldAddress field);

    /** Checks if the position is terminal.
     * @return {@code true} if one of the sides has uninterrupted sequence of {@value Constants#WIN_LENGTH} stones on the board
     */
    boolean isTerminal();

	/** Creates new position with given stone on given field.
	 * @param field Field address
	 * @param stone Stone to place
	 * @return New position with the stone on it
	 * @throws IndexOutOfBoundsException if the field is out of board
	 * @throws IllegalArgumentException  if the stone is {@link Stone#EMPTY EMPTY}
	 * @throws IllegalStateException     if there is already stone on the given field
	 */
	Position withStone(FieldAddress field, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

}
