package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Player;
import cz.doleckovi.piskvorky.api.board.Stone;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PositionImplTest {

	@Test
	void fields() {
		var board = new BoardImpl(5);
		var position = board.initialPosition();
		assertThat(position.fields())
				.allMatch(field -> field.getStone() == Stone.EMPTY);
		var index = board.toIndex(2, 2);
		position = position.setStone(index, Player.WHITE);
		assertThat(position.fields().get(index))
				.matches(field -> field.getStone() == Stone.WHITE);
	}

	@Test
	void terminate() {
		var board = new BoardImpl(7);
		var position = board.initialPosition();
		// two before
		position = position.setStone(board.toIndex(2, 3), Player.WHITE);
		position = position.setStone(board.toIndex(1, 3), Player.WHITE);
		// two after
		position = position.setStone(board.toIndex(4, 3), Player.WHITE);
		position = position.setStone(board.toIndex(5, 3), Player.WHITE);
		// this makes 5-in-row
		position = position.setStone(board.toIndex(3, 3), Player.WHITE);
		assertThat(position.isTerminal()).isTrue();
	}

}
