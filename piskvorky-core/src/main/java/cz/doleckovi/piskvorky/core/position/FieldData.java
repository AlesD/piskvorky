package cz.doleckovi.piskvorky.core.position;

import java.util.Objects;
import java.util.StringJoiner;

/** Field data. */
public class FieldData {

	public static final FieldData EMPTY = new FieldData(0);

	private final int move;

	private FieldData(int move) {
		this.move = move;
	}

    /** Checks if field is move candidate.
     * @return {@code true} if field should be considered for move
     */
	boolean isMoveCandidate() {
		return move >= CellData.MAX_MOVE;
	}

	FieldData withCell(CellData oldData, CellData newData) {
		return new FieldData(move - oldData.move() + newData.move());
	}

	Updater updater() {
		return new Updater(move);
	}

	static class Updater {

		private int move;

		public Updater(int move) {
			this.move = move;
		}

		void update(CellData oldData, CellData newData) {
			move = move - oldData.move() + newData.move();
		}

		FieldData newData() {
			return new FieldData(move);
		}

	}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FieldData fieldData = (FieldData) o;
        return move == fieldData.move;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(move);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FieldData.class.getSimpleName() + "[", "]")
                .add("move=" + move)
                .toString();
    }
}
