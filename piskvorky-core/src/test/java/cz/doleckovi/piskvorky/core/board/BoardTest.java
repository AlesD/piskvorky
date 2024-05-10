package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.core.evaluator.Pattern;
import org.assertj.core.api.Condition;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

class BoardTest implements WithAssertions {

	private static class DirectionCondition extends Condition<LineDescriptor> {
		DirectionCondition(Direction direction) {
			super(lineDescriptor -> direction.equals(lineDescriptor.direction()), "line is %s", direction);
		}
	}

	private static final Condition<LineDescriptor> HORIZONTAL_LINE = new DirectionCondition(Direction.HORIZONTAL);
	private static final Condition<LineDescriptor> VERTICAL_LINE = new DirectionCondition(Direction.VERTICAL);
	private static final Condition<LineDescriptor> DOWNHILL_LINE = new DirectionCondition(Direction.DOWNHILL);
	private static final Condition<LineDescriptor> UPHILL_LINE = new DirectionCondition(Direction.UPHILL);

	@ParameterizedTest
	@ValueSource(ints = {5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 100, 1000})
	void testLineCounts(int size) {
		var lineDescriptors = Board.generateLineDescriptors(size, Board.generateFieldDescriptors(size, Board.generateFieldAddresses(size)));
		assertThat(lineDescriptors)
				.haveExactly(size, HORIZONTAL_LINE)
				.haveExactly(size, VERTICAL_LINE)
				.haveExactly(2 * size - 9, DOWNHILL_LINE)
				.haveExactly(2 * size - 9, UPHILL_LINE);
		for (int length = 1; length < size; ++length)
			if (length < Pattern.LENGTH)
				assertThat(lineDescriptors)
						.filteredOn(LineDescriptor::length, length)
						.isEmpty();
			else
				assertThat(lineDescriptors)
						.filteredOn(LineDescriptor::length, length)
						.doNotHave(HORIZONTAL_LINE)
						.doNotHave(VERTICAL_LINE)
						.haveExactly(2, DOWNHILL_LINE)
						.haveExactly(2, UPHILL_LINE);
		assertThat(lineDescriptors)
				.filteredOn(LineDescriptor::length, size)
				.haveExactly(size, HORIZONTAL_LINE)
				.haveExactly(size, VERTICAL_LINE)
				.haveExactly(1, DOWNHILL_LINE)
				.haveExactly(1, UPHILL_LINE);
	}

	/** Board of size 6.
	 * <pre>
	 *          horizontal lines     vertical lines      downhill lines       uphill lines
	 *
	 *          0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5
	 *
	 *     0    0  0  0  0  0  0    6  7  8  9 10 11   13 12                           15 16   0
	 *     1    1  1  1  1  1  1    6  7  8  9 10 11   14 13 12                     15 16 17   1
	 *     2    2  2  2 (2) 2  2    6  7  8 (9)10 11      14 13(12)              15(16)17      2
	 *     3    3  3  3  3  3  3    6  7  8  9 10 11         14 13 12         15 16 17         3
	 *     4    4  4  4  4  4  4    6  7  8  9 10 11            14 13 12   15 16 17            4
	 *     5    5  5  5  5  5  5    6  7  8  9 10 11               14 13   16 17               5
	 *
	 *          0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5
	 * </pre>
	 */
	@Test
	void testLineAddresses() {
		var fieldDescriptor = new Board(6).fieldDescriptor(3, 2);
		var fieldAddress = fieldDescriptor.fieldAddress();
		assertThat(fieldDescriptor.lineAddresses())
				.containsExactly(
						Map.entry(Direction.HORIZONTAL, new LineAddress(2, 3, fieldAddress)),
						Map.entry(Direction.VERTICAL, new LineAddress(9, 2, fieldAddress)),
						Map.entry(Direction.DOWNHILL, new LineAddress(12, 2, fieldAddress)),
						Map.entry(Direction.UPHILL, new LineAddress(16, 3, fieldAddress)));
	}

}
