package cz.doleckovi.piskvorky.core.board;

import org.junit.jupiter.api.Test;

import static cz.doleckovi.piskvorky.api.board.Stone.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositionImplTest {

	private static final BoardImpl board = new BoardImpl(7);
	private static final FieldAddress address = board.fieldDescriptors().getFirst().fieldAddress();

	@Test
	void illegalArgumentsAreDetected() {
		assertThatThrownBy(() -> board.initialPosition().withStone(address.column(), address.row(), EMPTY))
				.isInstanceOf(IllegalArgumentException.class);

		assertThatThrownBy(() -> board.initialPosition().withStone(-1, address.row(), WHITE))
				.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> board.initialPosition().withStone(board.width(), address.row(), WHITE))
				.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> board.initialPosition().withStone(address.column(), -1, WHITE))
				.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> board.initialPosition().withStone(address.column(), board.height(), WHITE))
				.isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	void placingStones() {
		var position = board.initialPosition()
				.withStone(address.column(), address.row(), WHITE);

		assertThat(position.stone(address.column(), address.row()))
				.isSameAs(WHITE);
		assertThatThrownBy(() -> position.withStone(address.column(), address.row(), BLACK))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void canReachTerminalPosition() {
		PositionImpl position = board.initialPosition()
				// two before
				.withStone(address.column() - 2, address.row(), WHITE)
				.withStone(address.column() - 1, address.row(), WHITE)
				// two after
				.withStone(address.column() + 1, address.row(), WHITE)
				.withStone(address.column() + 2, address.row(), WHITE)
				// this makes 5-in-line
				.withStone(address.column(), address.row(), WHITE);

		assertThat(position.isTerminal())
				.isTrue();
		assertThatThrownBy(() -> position.withStone(0, 0, BLACK))
				.isInstanceOf(IllegalStateException.class);
	}

}
