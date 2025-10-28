package cz.doleckovi.piskvorky.api.board;

/** Field address.
 * <p>Helps to abstract internal organization of position.</p>
 */
public interface FieldAddress extends BoardObject {

	/** Gets column number.
	 * <p>Columns are numbered from 0.</p>
	 * @return Column number
	 */
	int column();

	/** Gets row number.
	 * <p>Rows are numbered from 0.</p>
	 * @return Row number
	 */
	int row();

	/** Gets address of cell.
	 * @param direction Line direction
	 * @return Cell address
	 */
	CellAddress cell(Direction direction);

}
