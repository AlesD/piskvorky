package cz.doleckovi.piskvorky.core.board;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

import static cz.doleckovi.piskvorky.api.Constants.DIRECTION_COUNT;
import static cz.doleckovi.piskvorky.api.Constants.WIN_LENGTH;

class Score {

	public static Score ZERO;
    public static Score WHITE_BEST;
    public static Score BLACK_BEST;

    static {
        var zero = new int[WIN_LENGTH + 1];
        ZERO = new Score(zero, zero);
        var best = zero.clone();
        best[WIN_LENGTH] = DIRECTION_COUNT * (2 * WIN_LENGTH - 1) + 1;
        WHITE_BEST = new Score(best, zero);
        BLACK_BEST = new Score(zero, best);
    }

	private final int[] white;
	private final int[] black;

	Score(int[] white, int[] black) {
        assert white.length == WIN_LENGTH + 1;
		assert black.length == white.length;
		this.white = white;
		this.black = black;
	}

    boolean isTerminal() {
        return white[WIN_LENGTH] > 0 || black[WIN_LENGTH] > 0;
    }

    int white(int index) {
        return white[index];
    }

    int black(int index) {
        return black[index];
    }

    Updater updater() {
        return new Updater(white.clone(), black.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return Objects.deepEquals(white, score.white) && Objects.deepEquals(black, score.black);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(white), Arrays.hashCode(black));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Score.class.getSimpleName() + "[", "]")
                .add("white=" + Arrays.toString(white))
                .add("black=" + Arrays.toString(black))
                .toString();
    }

    static class Updater {

        private final int[] white;
        private final int[] black;

        private Updater(int[] white, int[] black) {
            this.white = white;
            this.black = black;
        }

        void cellDataChanged(CellData oldData, CellData newData) {
            switch (oldData.stone()) {
                case WHITE:
                    --white[oldData.white()];
                    break;
                case BLACK:
                    --black[oldData.black()];
                    break;
            }
            switch (newData.stone()) {
                case WHITE:
                    ++white[newData.white()];
                    break;
                case BLACK:
                    ++black[newData.black()];
                    break;
            }
        }

        void scoreChanged(Score oldData, Score newData) {
            for (int index = WIN_LENGTH; index >= 0; --index) {
                white[index] = white[index] - oldData.white(index) + newData.white(index);
                black[index] = black[index] - oldData.black(index) + newData.black(index);
            }
        }

        Score newScore() {
            return new Score(white, black);
        }

    }

}
