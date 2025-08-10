package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Stone;

/** Line on the board. */
public interface Line {

	/** Line length.
	 * @return Line length
	 */
	int length();

	/** Determine if the line is terminal.
	 * @return true if the line contains 5-in-line
	 */
	boolean isTerminal();

	/** Gets stone at given offset.
	 * @param offset Offset from the beginning of line
	 * @return Stone at given offset
	 */
	Stone stone(int offset);

	/** Creates new line with given stone.
	 * @param offset Offset from the beginning of line
	 * @param stone Stone to place
	 * @return New line with the stone on it
	 * @throws IndexOutOfBoundsException if the offset is outside of line
	 * @throws IllegalArgumentException  if there is already stone other than EMPTY
	 * @throws IllegalStateException     if invoked on terminal line
	 */
	Line withStone(int offset, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

}
