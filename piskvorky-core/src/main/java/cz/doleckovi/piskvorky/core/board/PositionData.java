package cz.doleckovi.piskvorky.core.board;

import java.util.Objects;
import java.util.StringJoiner;

/** Position data. */
public class PositionData {

	public static final PositionData EMPTY = new PositionData(Score.ZERO);

    private final Score score;

	private PositionData(Score score) {
        this.score = score;
    }

    /** Checks if position is terminal.
     * @return {@code true} if position is terminal
     */
    public boolean isTerminal() {
        return score.isTerminal();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PositionData that = (PositionData) o;
        return Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(score);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PositionData.class.getSimpleName() + "[", "]")
                .add("score=" + score)
                .toString();
    }

    Updater updater() {
		return new Updater(score.updater());
	}

	static final class Updater {

        private final Score.Updater scoreUpdater;

		public Updater(Score.Updater scoreUpdater) {
            this.scoreUpdater = scoreUpdater;
		}

		void fieldDataChanged(FieldData oldData, FieldData newData) {
			// Nothing to do
		}

		void update(LineData oldData, LineData newData) {
			scoreUpdater.scoreChanged(oldData.score(), newData.score());
		}

		public PositionData newData() {
            var newScore = scoreUpdater.newScore();
            return new PositionData(newScore);
		}
	}

}
