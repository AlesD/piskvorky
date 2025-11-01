package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.position.CellData;
import cz.doleckovi.piskvorky.api.position.Stone;

/** Simple cell data. */
public class SimpleCellData implements CellData {

	public static int MOVE = 2;

	public static final SimpleCellData EMPTY = new SimpleCellData(Stone.EMPTY, 0, 0, 0);

	private final Stone stone;
	private final int white;
	private final int black;
	private final int move;

	private SimpleCellData(Stone stone, int white, int black, int move) {
		this.stone = stone;
		this.white = Integer.min(white, Board.minSize());
		this.black = Integer.min(black, Board.minSize());
		this.move = move;
	}

	/** Gets stone in the cell.
	 * @return Stone in the cell
	 */
	public Stone stone() {
		return stone;
	}

	/** Gets number of adjacent white stones.
	 * @return Number of adjacent white stones (including this cell) or zero for black (or blocked cell)
	 */
	int white() {
		return white;
	}

	/** Number of adjacent black stones.
	 * @return Number of adjacent black stones (including this cell) or zero for white (or blocked cell)
	 */
	int black() {
		return black;
	}

	/** Gets move probability.
	 * @return Zero for cells with stones, 2 for cells next to stone and decrease with distance
	 */
	int move() {
		return move;
	}

	/** Gets data for cell with stone.
	 * @param stone Stone to place in cell
	 * @return New cell data
	 * @throws IllegalStateException if {@code this.stone()} is not {@link Stone#EMPTY EMPTY}
	 * @throws IllegalArgumentException if the {@code stone} is {@link Stone#EMPTY EMPTY}
	 */
	SimpleCellData withStone(Stone stone) {
		return switch (stone) {
			case EMPTY -> throw new IllegalArgumentException("Stone is EMPTY");
			case WHITE -> new SimpleCellData(stone, white + 1, 0, 0);
			case BLACK -> new SimpleCellData(stone, 0, black + 1, 0);
			case BLOCK -> new SimpleCellData(stone, 0, 0, 0);
		};
	}

	SimpleCellData nextTo(SimpleCellData previous) {
		assert stone == Stone.EMPTY;
		return new SimpleCellData(Stone.EMPTY, previous.white(), previous.black(), MOVE);
	}

	SimpleCellData between(SimpleCellData previous, SimpleCellData next) {
		assert stone == Stone.EMPTY;
		return new SimpleCellData(Stone.EMPTY, previous.white() + next.white(), next.black() + next.black(), MOVE);
	}

	SimpleCellData asMoveCandidate(SimpleCellData previous) {
		assert stone == Stone.EMPTY;
		var move = Integer.max(previous.move() - 1, this.move);
		return (move == this.move) ? this : new SimpleCellData(Stone.EMPTY, white, black, move);
	}

}
