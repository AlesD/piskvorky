package cz.doleckovi.piskvorky.api.position;

import cz.doleckovi.piskvorky.api.Constants;

import java.util.List;

public interface Line<C extends Cell> {

	/** Cells on the line.
	 * @return Immutable list of cells
	 */
	List<C> cells();

	/** Checks if the line is terminal.
	 * @return {@code true} if position contains {@value Constants#SIZE} same stones in row
	 */
	boolean isTerminal();

	/** Creates new line with given stone.
	 * @param offset Offset from the beginning of line
	 * @param stone Stone to place
	 * @return New line with the stone on given offset
	 * @throws IndexOutOfBoundsException if given offset is not within line
	 * @throws IllegalArgumentException  if there is already stone on given offset
	 * @throws IllegalStateException     if invoked on terminal position
	 */
	Line<C> withStone(int offset, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

}
