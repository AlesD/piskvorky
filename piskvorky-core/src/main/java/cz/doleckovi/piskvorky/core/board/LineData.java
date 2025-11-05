package cz.doleckovi.piskvorky.core.board;

import java.util.Objects;
import java.util.StringJoiner;

public class LineData {

	static final LineData EMPTY = new LineData(Score.ZERO);

    private final Score score;

	LineData(Score score) {
        this.score = score;
	}

    Score score() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LineData lineData = (LineData) o;
        return Objects.equals(score, lineData.score);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(score);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LineData.class.getSimpleName() + "[", "]")
                .add("score=" + score)
                .toString();
    }

    Updater updater() {
        return new Updater(score.updater());
    }

    static class Updater {

        private final Score.Updater scoreUpdater;

        private Updater(Score.Updater scoreUpdater) {
            this.scoreUpdater = scoreUpdater;
        }

        void update(CellData oldData, CellData newData) {
            scoreUpdater.cellDataChanged(oldData, newData);
        }

        LineData newLine() {
            return new LineData(scoreUpdater.newScore());
        }

    }

}
