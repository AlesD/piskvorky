package cz.doleckovi.piskvorky.core.board;
//
//import cz.doleckovi.piskvorky.api.board.Direction;
//import cz.doleckovi.piskvorky.api.board.FieldIterator;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class FieldIteratorImplTest {
//
//	record Fixture(
//			Board board,
//			Position position,
//			FieldIterator<Field> iterator,
//			FieldIterator<Field> reverse
//	) {}
//
//	record Coordinates(int column, int line) {}
//
//	private Fixture setup(Board board, int column, int line, Direction direction) {
//		var position = board.initialPosition();
//		var iterator = position.iterator(board.toIndex(column, line), direction);
//		var reverse = iterator.reverseIterator();
//		return new Fixture(board, position, iterator, reverse);
//	}
//
//	@Test
//	void leftIterator() {
////		var fixture = setup(new Board(5), 2, 2, Direction.LEFT);
////		assertThat(fixture.iterator).toIterable().contains(
////				fixture.position.fields().get(fixture.board.toIndex(3, 2)),
////				fixture.position.fields().get(board.toIndex(4, 2)));
////		assertThat(reverse).toIterable().contains(
////				position.fields().get(board.toIndex(1, 2)),
////				position.fields().get(board.toIndex(0, 2)));
//	}
//
//	@Test
//	void downIterator() {
//		var board = new Board(5);
//		var position = board.initialPosition();
//		var iterator = position.iterator(board.toIndex(2, 2), Direction.VERTICAL);
//		var reverse = iterator.reverseIterator();
//		assertThat(iterator).toIterable().contains(
//				position.fields().get(board.toIndex(2, 3)),
//				position.fields().get(board.toIndex(2, 4)));
//		assertThat(reverse).toIterable().contains(
//				position.fields().get(board.toIndex(2, 1)),
//				position.fields().get(board.toIndex(2, 0)));
//	}
//
//	@Test
//	void rightDownIteratorAboveDiagonal() {
//		var board = new Board(5, 6);
//		var position = board.initialPosition();
//		var iterator = position.iterator(board.toIndex(2, 1), Direction.DOWN_RIGHT);
//		var reverse = iterator.reverseIterator();
//		assertThat(iterator).toIterable().contains(
//				position.fields().get(board.toIndex(3, 2)),
//				position.fields().get(board.toIndex(4, 3)));
//		assertThat(reverse).toIterable().contains(
//				position.fields().get(board.toIndex(1, 0)));
//	}
//
//	@Test
//	void rightDownIteratorBelowDiagonal() {
////		var board = new Board(6, 5);
////		var position = board.initialPosition();
////		var iterator = board.iterator(board.toIndex(1, 2), Direction.DOWN_RIGHT);
////		var reverse = iterator.reverseIterator();
////		assertThat(iterator).toIterable().contains(board.toIndex(2, 3), board.toIndex(3, 4));
////		assertThat(reverse).toIterable().contains(board.toIndex(0, 1));
//	}
//
//	@Test
//	void leftDownIteratorAboveDiagonal() {
////		var board = new Board(5, 6);
////		var position = board.initialPosition();
////		var iterator = board.iterator(board.toIndex(2, 1), Direction.LEFT_DOWN);
////		var reverse = iterator.reverseIterator();
////		assertThat(iterator).toIterable().contains(board.toIndex(1, 2), board.toIndex(0, 3));
////		assertThat(reverse).toIterable().contains(board.toIndex(3, 0));
//	}
//
//	@Test
//	void leftDownIteratorBelowDiagonal() {
////		var board = new Board(6, 5);
////		var iterator = board.iterator(board.toIndex(4, 2), Direction.LEFT_DOWN);
////		var reverse = iterator.reverseIterator();
////		assertThat(iterator).toIterable().contains(board.toIndex(3, 3), board.toIndex(2, 4));
////		assertThat(reverse).toIterable().contains(board.toIndex(5, 1));
//	}
//
//	@Test
//	void reverseIteratorHasSamePrevious() {
////		var board = new Board(5);
////		var fieldIndex = board.toIndex(0, 0);
////		var iterator = board.iterator(fieldIndex, Direction.LEFT);
////		var reverseIterator = iterator.reverseIterator();
////		// Previous is the fieldIndex used to create it
////		assertThat(iterator.hasPrevious()).isTrue();
////		assertThat(iterator.previousInt()).isEqualTo(fieldIndex);
////		// Reverse iterator has same previous
////		assertThat(reverseIterator.previousInt()).isEqualTo(fieldIndex);
//	}
//
//}
