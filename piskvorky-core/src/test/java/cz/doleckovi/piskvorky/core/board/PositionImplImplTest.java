package cz.doleckovi.piskvorky.core.board;

import org.junit.jupiter.api.Test;

import static cz.doleckovi.piskvorky.api.board.Stone.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

class PositionImplImplTest {

	@Test
	void fields() {
		var board = new BoardImpl(5);
		var position = board.initialPosition().withStone(2, 2, WHITE);
		assertThat(position.stone(2, 2)).isSameAs(WHITE);
	}

	@Test
	void terminate() {
		var board = new BoardImpl(7);
		PositionImpl position = board.initialPosition()
				// two before
				.withStone(2, 3, WHITE)
				.withStone(1, 3, WHITE)
				// two after
				.withStone(4, 3, WHITE)
				.withStone(5, 3, WHITE)
				// this makes 5-in-line
				.withStone(3, 3, WHITE);
		assertThat(position.isTerminal()).isTrue();
	}

}
