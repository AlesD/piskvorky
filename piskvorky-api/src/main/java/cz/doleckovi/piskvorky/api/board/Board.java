package cz.doleckovi.piskvorky.api.board;

/** Board on which games are played. */
public interface Board {

    /** Gets board size.
     * @return Board size
     */
    int size();

    /** Gets field address.
     * @param column Column number
     * @param row Row number
     * @return Address of field at given board coordinates
     * @throws IndexOutOfBoundsException if either column or row is out of board
     */
    FieldAddress field(int column, int row) throws IndexOutOfBoundsException;

    /** Gets line descriptor.
     * @param cell Cell address
     * @return Address of line with given index
     * @throws IllegalArgumentException if the cell is from different board
     */
	LineDescriptor line(CellAddress cell) throws IllegalArgumentException;

    /** Translates cell address to field address.
     * @param cell Cell address
     * @return Address of matching field
     * @throws IndexOutOfBoundsException if the cell is out of board
     */
    default FieldAddress field(CellAddress cell) throws IndexOutOfBoundsException {
        return line(cell).field(cell.offset());
    }

}
