package cz.doleckovi.piskvorky.api.board;

import java.util.Collection;

/** Board on which games are played.
 * <p>Board instances must be thread safe and immutable.</p>
 */
public interface Board extends BoardObject {

	/** Gets number of lines on the board.
	 * @return Number of lines
	 */
	int lineCount();

	LineDescriptor line(int index);

	/** Get address of field at given column and row.
	 * <p>Use of field addresses abstracts internal organization of position data.</p>
	 * @param column Column number
	 * @param row Row number
	 * @return Cell address
	 * @throws IndexOutOfBoundsException if either column or row is out og board
	 */
	FieldAddress field(int column, int row) throws IndexOutOfBoundsException;

	FieldAddress field(CellAddress cell);

	/** Gets address of all cells from all lines that pass through given field.
	 * @param field Filed address
	 * @return Cell addresses
	 * @throws IllegalArgumentException if field address is wrong
	 */
	Collection<CellAddress> cells(FieldAddress field);

}
