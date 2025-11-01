package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.Immutable;

/** Field address.
 * <p>Helps to abstract internal organization of position.</p>
 */
public interface FieldAddress extends Immutable {

    /** Gets field index.
     * @return Field index
     */
    int index();

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
	 * <p>Note that fields in corners might be cells for digonal lines.</p>
	 * @param direction Line direction
	 * @return Cell address or {@code null}
	 */
	CellAddress cell(Direction direction);

}
