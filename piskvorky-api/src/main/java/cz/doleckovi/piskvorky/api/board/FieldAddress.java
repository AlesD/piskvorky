package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.Constants;

import java.util.EnumMap;
import java.util.Map;

import static cz.doleckovi.piskvorky.api.Constants.WIN_LENGTH;

/** Field address.
 * <p>Helps to abstract internal organization of position.</p>
 */
public final class FieldAddress {

    private final Board board;
    private final int index;
    private final int column;
    private final int row;
    private final EnumMap<Direction, CellAddress> cells = new EnumMap<>(Direction.class);

    public FieldAddress(Board board, int index, int column, int row, Map<Direction, CellAddress> cells) {
		this.board = board;
        this.index = index;
        this.column = column;
        this.row = row;
        this.cells.putAll(cells);
    }

	/** Gets board to which this field address belongs.
	 * @return Board instance
	 */
	public Board board() {
		return board;
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
	 * <p>Board might not contain lines that are shorter than {@value Constants#WIN_LENGTH} in which case this method
	 * may return no address.</p>
	 * @param direction Line direction
	 * @return Cell address or {@code null}
	 */
	public CellAddress cell(Direction direction) {
        return cells.get(direction);
    }

}
