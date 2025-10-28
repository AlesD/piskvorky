package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.position.Stone;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class SimpleBoardTest extends BoardTest<SimpleBoard> {

    @Override
    protected SimpleBoard board(final int size) {
        return new SimpleBoard(size);
    }

    @Test
	void allowsMinimalBoardSize() {
		assertThatCode(() -> new SimpleBoard(Constants.SIZE))
				.doesNotThrowAnyException();
	}
	@Test

	void doesNotAllowBoardSmallerThanMinimalSize() {
		assertThatThrownBy(() -> new SimpleBoard(Constants.SIZE - 1))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void providesInitialPosition() {
		var board = new SimpleBoard(Constants.SIZE);
		var position = board.initialPosition();
		IntStream.range(0, board.width()).forEach(column ->
				IntStream.range(0, board.height()).forEach(row ->
						assertThat(position.stone(column, row)).isEqualTo(Stone.EMPTY)));
	}

}
