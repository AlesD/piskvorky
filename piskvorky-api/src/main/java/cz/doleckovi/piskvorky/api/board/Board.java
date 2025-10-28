package cz.doleckovi.piskvorky.api.board;

import java.util.Collection;

import cz.doleckovi.piskvorky.api.Immutable;

/** Board on which games are played. */
public interface Board extends Immutable {

    /** Gets board size.
     * @return Board size
     */
    int size();

	/** Gets number of lines on the board.
	 * @return Number of lines
	 */
	int lineCount();

    /** Gets line descriptor.
     * @param index Line index
     * @return Descriptor of line with given index
     * @throws IndexOutOfBoundsException if index is less than zero or greater or equal than line count
     */
	LineDescriptor line(int index) throws IndexOutOfBoundsException;

    /** Gets number of fields.
     * <p>Computed value: {@code size * size}</p>
     * @return Number of fields
     */
    default int fieldCount() {
        return size() * size();
    }

    /** Ges field address.
     * @param index Field index
     * @return Address of field with given index
     * @throws IndexOutOfBoundsException if index is less than zero or greater or equal than field count
     */
    FieldAddress field(int index) throws IndexOutOfBoundsException;

	/** Gets field address.
	 * @param column Column number
	 * @param row Row number
	 * @return Address of field at given board coordinates
	 * @throws IndexOutOfBoundsException if either column or row is out of board
	 */
	FieldAddress field(int column, int row) throws IndexOutOfBoundsException;

    /** Translates cell address to field address.
     * @param cell Cell address
     * @return Address of matching field
     * @throws IndexOutOfBoundsException if the cell is out of board
     */
	default FieldAddress field(CellAddress cell) throws IndexOutOfBoundsException {
        return line(cell.line()).field(cell.offset());
    }

    /** Gets cell address.
     * @param line Line index
     * @param offset Offset of cell within line
     * @return Cell address
     * @throws IndexOutOfBoundsException if either line or offer is out of board
     */
    CellAddress cell(int line, int offset) throws IndexOutOfBoundsException;

	/** Translates cell address to field addresses.
	 * @param field Filed address
	 * @return Cell addresses
	 * @throws IllegalArgumentException if field address is wrong
	 */
	Collection<? extends CellAddress> cells(FieldAddress field);

}
