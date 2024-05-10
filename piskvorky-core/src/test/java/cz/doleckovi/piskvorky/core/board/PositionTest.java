package cz.doleckovi.piskvorky.core.board;
//
//import cz.doleckovi.piskvorky.api.board.Player;
//import cz.doleckovi.piskvorky.api.board.Position;
//import cz.doleckovi.piskvorky.api.board.Stone;
//import org.junit.jupiter.api.Test;
//import static org.assertj.core.api.Assertions.*;
//
//class PositionTest {
//
//	@Test
//	void fields() {
//		var board = new Board(5);
//		Position position = board.initialPosition();
//		assertThat(position.fields())
//				.allMatch(fieldDescriptor -> fieldDescriptor.getStone() == Stone.EMPTY);
//		position = position.setStone(2, 2, Player.WHITE);
//		assertThat(position.getStone(2, 2)).isSameAs(Stone.WHITE);
//	}
//
//	@Test
//	void terminate() {
//		var board = new Board(7);
//		Position position = board.initialPosition();
//		// two before
//		position = position.setStone(2, 3, Player.WHITE);
//		position = position.setStone(1, 3, Player.WHITE);
//		// two after
//		position = position.setStone(4, 3, Player.WHITE);
//		position = position.setStone(5, 3, Player.WHITE);
//		// this makes 5-in-line
//		position = position.setStone(3, 3, Player.WHITE);
//		assertThat(position.isTerminal()).isTrue();
//	}
//
//}
