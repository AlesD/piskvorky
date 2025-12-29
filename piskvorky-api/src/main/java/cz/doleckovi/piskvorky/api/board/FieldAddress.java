package cz.doleckovi.piskvorky.api.board;

import java.util.EnumMap;
import java.util.Map;

/** Field address.
 * <p>Helps to abstract internal organization of position.</p>
 */
public final class FieldAddress {

    private final int index;
    private final int column;
    private final int row;
    private final EnumMap<Direction, CellAddress> cells = new EnumMap<>(Direction.class);

    public FieldAddress(int index, int column, int row, Map<Direction, CellAddress> cells) {
        this.index = index;
        this.column = column;
        this.row = row;
        this.cells.putAll(cells);
    }


    /** Gets field index.
     * <p>Fields can be ordered in any possible way.</p>
     * @return Field index
     */
    public int index() {
        return index;
    }

	/** Gets column number.
	 * <p>Columns are numbered from 0.</p>
	 * @return Column number
	 */
	public int column() {
        return column;
    }

	/** Gets row number.
	 * <p>Rows are numbered from 0.</p>
	 * @return Row number
	 */
	public int row() {
        return row;
    }

	/** Gets address of cell.
	 * <p>Note that fields in corners might be cells for digonal lines.</p>
	 * @param direction Line direction
	 * @return Cell address or {@code null}
	 */
	public CellAddress cell(Direction direction) {
        return cells.get(direction);
    }

}
