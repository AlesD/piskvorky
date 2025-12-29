package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.core.position.PositionImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static cz.doleckovi.piskvorky.api.Constants.WIN_LENGTH;

class SimplePositionTest implements WithAssertions {

	static BoardImpl board = new BoardImpl(WIN_LENGTH);

	@Test
	void detectionOfTerminalPositionsWork() {
		var position = new PositionImpl(board)
				// Downhill without stone at end
				.withStone(board.field(0, 0), Stone.WHITE)
				.withStone(board.field(1, 1), Stone.WHITE)
				.withStone(board.field(2, 2), Stone.WHITE)
				.withStone(board.field(3, 3), Stone.WHITE)
				// 5th stone at 4, 4 is missing

				// Vertical without stone at beginning
				// 1st stone at 1, 0 is missing
				// There is already stone at 1, 1
				.withStone(board.field(1, 2), Stone.WHITE)
				.withStone(board.field(1, 3), Stone.WHITE)
				.withStone(board.field(1, 4), Stone.WHITE)

				// Horizontal without stone in middle
				.withStone(board.field(0, 3), Stone.WHITE)
				// There is already stone at 1, 3
				// 3rd stone at 2, 3 is missing
				// There is already stone at 3, 3
				.withStone(board.field(4, 3), Stone.WHITE)

				// Uphill without stone inside but not in middle
				.withStone(board.field(0, 4), Stone.WHITE)
				// There is already stone at 1, 3
				// There is already stone at 2, 2
				// 4th stone at 3, 1 is missing
				.withStone(board.field(4, 0), Stone.WHITE);

		assertThat(position.isTerminal())
				.as("Position is not terminal")
				.isFalse();
		assertThat(position.withStone(board.field(4, 4), Stone.WHITE).isTerminal())
				.as("By adding 5th stone at end of downhill line position becomes terminal")
				.isTrue();
		assertThat(position.withStone(board.field(1, 0), Stone.WHITE).isTerminal())
				.as("By adding 1st stone at beginning of vertical line position becomes terminal")
				.isTrue();
		assertThat(position.withStone(board.field(2, 3), Stone.WHITE).isTerminal())
				.as("By adding 3rd stone in middle of horizontal line position becomes terminal")
				.isTrue();
		assertThat(position.withStone(board.field(3, 1), Stone.WHITE).isTerminal())
				.as("By adding 4th stone inside uphill line position becomes terminal")
				.isTrue();
	}

}
