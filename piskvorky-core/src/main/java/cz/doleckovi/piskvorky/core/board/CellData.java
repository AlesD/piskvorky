package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.Objects;
import java.util.StringJoiner;

import static cz.doleckovi.piskvorky.api.Constants.WIN_LENGTH;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

/** Cell data. */
public class CellData {

    public static final CellData EMPTY = new CellData(Stone.EMPTY, 0, 0, 0);

    static final int MAX_MOVE = 2;

	private final Stone stone;
	private final int white;
	private final int black;
	private final int move;

	private CellData(Stone stone, int white, int black, int move) {
		this.stone = stone;
		this.white = white;
		this.black = black;
		this.move = move;
	}

	/** Gets stone in the cell.
	 * @return Stone in the cell
	 */
	public Stone stone() {
		return stone;
	}

	/** Gets number of adjacent white stones.
	 * @return Number of adjacent white stones (including this cell) or zero for black (or blocked) cell
	 */
	int white() {
		return white;
	}

	/** Number of adjacent black stones.
	 * @return Number of adjacent black stones (including this cell) or zero for white (or blocked) cell
	 */
	int black() {
		return black;
	}

	/** Gets move suitability level.
     * <p>Cells with stones have zero suitability for move. Cells next to cell with stone have maximum suitability.
     * Other cells have suitability defined as maximum of suitabilities of adjacent cells minus one, but never
     * negative.</p>
     */
	int move() {
		return move;
	}

	/** Creates data for cell with stone.
	 * @param stone Stone to place in cell
	 * @return New cell data
	 * @throws IllegalStateException if {@code this.stone()} is not {@link Stone#EMPTY EMPTY}
	 * @throws IllegalArgumentException if the {@code stone} is {@link Stone#EMPTY EMPTY}
	 */
	CellData withStone(Stone stone) throws IllegalStateException, IllegalArgumentException {
        if (this.stone != Stone.EMPTY)
            throw new IllegalStateException("Cell is not EMPTY");
		return switch (stone) {
			case EMPTY -> throw new IllegalArgumentException("Stone is EMPTY");
			case WHITE -> new CellData(stone, min(white + 1, WIN_LENGTH), 0, 0);
			case BLACK -> new CellData(stone, 0, min(black + 1, WIN_LENGTH), 0);
			case BLOCK -> new CellData(stone, 0, 0, 0);
		};
	}

    /** Creates data for empty cell next to cell with stone.
     * <p>This method is used for empty cells on the edge of board.</p>
     * @param previous Cell with stone
     * @return New cell data
     * @throws IllegalStateException if {@code this.stone()} is not {@link Stone#EMPTY EMPTY}
     * @throws IllegalArgumentException if the {@code previous.stone()} is {@link Stone#EMPTY EMPTY}
     */
	CellData nextTo(CellData previous) throws IllegalStateException, IllegalArgumentException {
        if (this.stone != Stone.EMPTY)
            throw new IllegalStateException("Cell is not EMPTY");
        if (previous.stone() == Stone.EMPTY)
            throw new IllegalArgumentException("Previous cell is EMPTY");
		return new CellData(Stone.EMPTY, previous.white(), previous.black(), MAX_MOVE);
	}

    /** Creates data for empty cell between cell with stone and other cell.
     * @param previous Cell with stone
     * @param next Other cell
     * @return New cell data
     * @throws IllegalStateException if {@code this.stone()} is not {@link Stone#EMPTY EMPTY}
     * @throws IllegalArgumentException if the {@code previous.stone()} is {@link Stone#EMPTY EMPTY}
     */
	CellData between(CellData previous, CellData next) throws IllegalStateException, IllegalArgumentException {
        if (this.stone != Stone.EMPTY)
            throw new IllegalStateException("Cell is not EMPTY");
        if (previous.stone() == Stone.EMPTY)
            throw new IllegalArgumentException("Previous cell is EMPTY");
		return new CellData(Stone.EMPTY, min(previous.white() + next.white(), WIN_LENGTH),
                min(next.black() + next.black(), WIN_LENGTH), MAX_MOVE);
	}

    /** Updates move suitability of empty cell.
     * @param previous Other empty cell
     * @return New cell data with altered level of move suitability or {code this} it the move suitability did not change
     * @throws IllegalStateException if {@code this.stone()} is not {@link Stone#EMPTY EMPTY}
     * @throws IllegalArgumentException if the {@code previous.stone()} is not {@link Stone#EMPTY EMPTY}
     */
	CellData asMoveCandidate(CellData previous) throws IllegalStateException, IllegalArgumentException {
        if (this.stone != Stone.EMPTY)
            throw new IllegalStateException("Cell is not EMPTY");
        if (previous.stone() != Stone.EMPTY)
            throw new IllegalArgumentException("Previous cell is not EMPTY");
		var moveCandidate = max(previous.move() - 1, this.move);
		return (moveCandidate == this.move) ? this : new CellData(Stone.EMPTY, white, black, moveCandidate);
	}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CellData cellData = (CellData) o;
        return white == cellData.white && black == cellData.black && move == cellData.move && stone == cellData.stone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stone, white, black, move);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CellData.class.getSimpleName() + "[", "]")
                .add("stone=" + stone)
                .add("white=" + white)
                .add("black=" + black)
                .add("move=" + move)
                .toString();
    }

}
