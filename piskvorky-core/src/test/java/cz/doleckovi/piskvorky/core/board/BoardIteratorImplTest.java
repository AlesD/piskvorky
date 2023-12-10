package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;
import org.junit.jupiter.api.Test;

import java.util.ListIterator;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardIteratorImplTest {

	@Test
	void leftIterator() {
		var board = new BoardImpl(5);
		var iterator = board.iterator(board.toIndex(2, 2), Direction.LEFT);
		var reverse = iterator.reverseIterator();
		assertThat(iterator).toIterable().contains(board.toIndex(3, 2), board.toIndex(4, 2));
		assertThat(reverse).toIterable().contains(board.toIndex(1, 2), board.toIndex(0, 2));
	}

	@Test
	void downIterator() {
		var board = new BoardImpl(5);
		var iterator = board.iterator(board.toIndex(2, 2), Direction.DOWN);
		var reverse = iterator.reverseIterator();
		assertThat(iterator).toIterable().contains(board.toIndex(2, 3), board.toIndex(2, 4));
		assertThat(reverse).toIterable().contains(board.toIndex(2, 1), board.toIndex(2, 0));
	}

	@Test
	void rightDownIteratorAboveDiagonal() {
		var board = new BoardImpl(5, 6);
		var iterator = board.iterator(board.toIndex(2, 1), Direction.DOWN_RIGHT);
		var reverse = iterator.reverseIterator();
		assertThat(iterator).toIterable().contains(board.toIndex(3, 2), board.toIndex(4, 3));
		assertThat(reverse).toIterable().contains(board.toIndex(1, 0));
	}

	@Test
	void rightDownIteratorBelowDiagonal() {
		var board = new BoardImpl(6, 5);
		var iterator = board.iterator(board.toIndex(1, 2), Direction.DOWN_RIGHT);
		var reverse = iterator.reverseIterator();
		assertThat(iterator).toIterable().contains(board.toIndex(2, 3), board.toIndex(3, 4));
		assertThat(reverse).toIterable().contains(board.toIndex(0, 1));
	}

	@Test
	void leftDownIteratorAboveDiagonal() {
		var board = new BoardImpl(5, 6);
		var iterator = board.iterator(board.toIndex(2, 1), Direction.LEFT_DOWN);
		var reverse = iterator.reverseIterator();
		assertThat(iterator).toIterable().contains(board.toIndex(1, 2), board.toIndex(0, 3));
		assertThat(reverse).toIterable().contains(board.toIndex(3, 0));
	}

	@Test
	void leftDownIteratorBelowDiagonal() {
		var board = new BoardImpl(6, 5);
		var iterator = board.iterator(board.toIndex(4, 2), Direction.LEFT_DOWN);
		var reverse = iterator.reverseIterator();
		assertThat(iterator).toIterable().contains(board.toIndex(3, 3), board.toIndex(2, 4));
		assertThat(reverse).toIterable().contains(board.toIndex(5, 1));
	}

	@Test
	void reverseIteratorHasSamePrevious() {
		var board = new BoardImpl(5);
		var index = board.toIndex(0, 0);
		var iterator = board.iterator(index, Direction.LEFT);
		var reverseIterator = iterator.reverseIterator();
		// Previous is the index used to create it
		assertThat(iterator.hasPrevious()).isFalse();
		assertThat(iterator.previous()).isEqualTo(index);
		// Reverse iterator has same previous
		assertThat(reverseIterator.previous()).isEqualTo(index);
	}

}
