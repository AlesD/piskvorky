package cz.doleckovi.piskvorky.api.board;

/** Board on which games are played. */
public interface Board {

    /** Gets board size.
     * @return Board size
     */
    int size();

    /** Number of fields on the board.
     * <p>Note: Boards with irregular shapes or holes might exist.</p>
     * @return Number of fields
     */
    int fieldCount();

    /** Gets field address.
     * @param index Field index
     * @return Address with given index
     * @throws IndexOutOfBoundsException if the index is negative or greater than or equal to field count
     */
    FieldAddress field(int index) throws IndexOutOfBoundsException;

    /** Gets field address for given column and row.
     * <p>Note: Boards with irregular shapes or holes might exist.</p>
     * @param column Column number
     * @param row Row number
     * @return Address of field at given board coordinates or {@code null}
     * @throws IndexOutOfBoundsException if either column or row is out of board
     */
    FieldAddress field(int column, int row) throws IndexOutOfBoundsException;

    /** Gets number of lines on the board.
     * @return Number of lines
     */
    int lineCount();

    /** Gets line descriptor.
     * @param index Line index
     * @return Descriptor for line with given index
     * @throws IndexOutOfBoundsException if the index is negative or greater than or equal to line count
     */
    LineDescriptor line(int index) throws IndexOutOfBoundsException;

    /** Translates cell address to field address.
     * @param cell Cell address
     * @return Address of matching field
     * @throws IndexOutOfBoundsException if the cell is out of board
     */
    default FieldAddress field(CellAddress cell) throws IndexOutOfBoundsException {
        return line(cell.line()).field(cell.offset());
    }

}
