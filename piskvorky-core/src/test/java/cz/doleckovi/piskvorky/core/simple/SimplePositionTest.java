package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimplePositionTest implements WithAssertions {

	@Test
	void detectionOfTerminalPositionsWork() {
		var position = new SimpleBoard(Constants.SIZE).initialPosition()
				// Downhill without stone at end
				.withStone(0, 0, Stone.WHITE)
				.withStone(1, 1, Stone.WHITE)
				.withStone(2, 2, Stone.WHITE)
				.withStone(3, 3, Stone.WHITE)
				// 5th stone at 4, 4 is missing

				// Vertical without stone at beginning
				// 1st stone at 1, 0 is missing
				// There is already stone at 1, 1
				.withStone(1, 2, Stone.WHITE)
				.withStone(1, 3, Stone.WHITE)
				.withStone(1, 4, Stone.WHITE)

				// Horizontal without stone in middle
				.withStone(0, 3, Stone.WHITE)
				// There is already stone at 1, 3
				// 3rd stone at 2, 3 is missing
				// There is already stone at 3, 3
				.withStone(4, 3, Stone.WHITE)

				// Uphill without stone inside but not in middle
				.withStone(0, 4, Stone.WHITE)
				// There is already stone at 1, 3
				// There is already stone at 2, 2
				// 4th stone at 3, 1 is missing
				.withStone(4, 0, Stone.WHITE);

		assertThat(position.isTerminal())
				.as("Position is not terminal")
				.isFalse();
		assertThat(position.withStone(4, 4, Stone.WHITE).isTerminal())
				.as("By adding 5th stone at end of downhill line position becomes terminal")
				.isTrue();
		assertThat(position.withStone(1, 0, Stone.WHITE).isTerminal())
				.as("By adding 1st stone at beginning of vertical line position becomes terminal")
				.isTrue();
		assertThat(position.withStone(2, 3, Stone.WHITE).isTerminal())
				.as("By adding 3rd stone in middle of horizontal line position becomes terminal")
				.isTrue();
		assertThat(position.withStone(3, 1, Stone.WHITE).isTerminal())
				.as("By adding 4th stone inside uphill line position becomes terminal")
				.isTrue();
	}

	@Test
	void doesNotAllowPuttingStoneInTerminalPosition() {
		var position = new SimpleBoard(Constants.SIZE).initialPosition()
				.withStone(0, 0, Stone.BLACK)
				.withStone(1, 1, Stone.BLACK)
				.withStone(2, 2, Stone.BLACK)
				.withStone(3, 3, Stone.BLACK)
				.withStone(4, 4, Stone.BLACK);
		assertThat(position.isTerminal())
				.as("Position is terminal")
				.isTrue();
		assertThatThrownBy(() -> position.withStone(0, 2, Stone.WHITE))
				.as("Adding stone to terminal position fails")
				.isInstanceOf(IllegalStateException.class);
	}

}