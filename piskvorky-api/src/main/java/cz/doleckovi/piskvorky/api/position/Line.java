package cz.doleckovi.piskvorky.api.position;

/** Line on board.
 * <p>Line must be thread safe.</p>
 * @param <L> Line data type
 * @param <C> Cell data type
 */
public interface Line<L extends LineData, C extends CellData> {

	/** Gets line data.
	 * @return Line data
	 */
	L lineData();

	/** Gets cell count
	 * @return Cell count
	 */
	int cellCount();

	/** Gets cell data.
     * @param offset Cell offset
     * @return Cell data
     */
    C cellData(int offset);

    /** Creates new line with given stone.
	 * @param offset Offset from the beginning of line
	 * @param stone Stone to place
     * @param callback Receiver of callbacks for data changes
	 * @return New line with the given stone
	 * @throws IndexOutOfBoundsException if given offset is not within line
	 * @throws IllegalArgumentException  if the stone is {@link Stone#EMPTY empty}
	 * @throws IllegalStateException     if invoked on terminal position or if there is already stone at given offset
	 */
	Line<L, C> withStone(int offset, Stone stone, LineCallback<L, C> callback)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

}
