package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class SimpleBoardTest implements WithAssertions {

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