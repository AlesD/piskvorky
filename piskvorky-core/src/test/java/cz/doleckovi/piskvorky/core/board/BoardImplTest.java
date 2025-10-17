package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.Constants;
import org.assertj.core.api.Condition;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.StringJoiner;

class BoardImplTest implements WithAssertions {

	private static class DirectionCondition extends Condition<LineDescriptor> {
		DirectionCondition(Direction direction) {
			super(lineDescriptor -> direction.equals(lineDescriptor.direction()), "line is %s", direction);
		}
	}

	private static final Condition<LineDescriptor> HORIZONTAL_LINES = new DirectionCondition(Direction.HORIZONTAL);
	private static final Condition<LineDescriptor> VERTICAL_LINES = new DirectionCondition(Direction.VERTICAL);
	private static final Condition<LineDescriptor> DOWNHILL_LINES = new DirectionCondition(Direction.DOWNHILL);
	private static final Condition<LineDescriptor> UPHILL_LINES = new DirectionCondition(Direction.UPHILL);

	@ParameterizedTest
	@ValueSource(ints = {5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 100, 1000})
	void testLineCounts(int size) {
		var lineDescriptors = BoardImpl.generateLineDescriptors(size, BoardImpl.generateFieldDescriptors(size, BoardImpl.generateFieldAddresses(size)));
		assertThat(lineDescriptors)
				.haveExactly(size, HORIZONTAL_LINES)
				.haveExactly(size, VERTICAL_LINES)
				.haveExactly(2 * size - 9, DOWNHILL_LINES)
				.haveExactly(2 * size - 9, UPHILL_LINES);
		for (int length = 1; length < size; ++length)
			if (length < Constants.SIZE)
				assertThat(lineDescriptors)
						.filteredOn(LineDescriptor::length, length)
						.isEmpty();
			else
				assertThat(lineDescriptors)
						.filteredOn(LineDescriptor::length, length)
						.doNotHave(HORIZONTAL_LINES)
						.doNotHave(VERTICAL_LINES)
						.haveExactly(2, DOWNHILL_LINES)
						.haveExactly(2, UPHILL_LINES);
		assertThat(lineDescriptors)
				.filteredOn(LineDescriptor::length, size)
				.haveExactly(size, HORIZONTAL_LINES)
				.haveExactly(size, VERTICAL_LINES)
				.haveExactly(1, DOWNHILL_LINES)
				.haveExactly(1, UPHILL_LINES);
	}

	/** Board of size 6.
	 * <pre>
	 *          horizontal lines     vertical lines      downhill lines       uphill lines        field index
	 *
	 *          0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5
	 *
	 *     0    0  0  0  0  0  0    6  7  8  9 10 11   13 12                           15 16   35 33 27 23 30 34   0
	 *     1    1  1  1  1  1  1    6  7  8  9 10 11   14 13 12                     15 16 17   31 22 14 10 16 24   1
	 *     2    2  2  2 (2) 2  2    6  7  8 (9)10 11      14 13(12)              15(16)17      28 13  5 (2) 6 17   2
	 *     3    3  3  3  3  3  3    6  7  8  9 10 11         14 13 12         15 16 17         21  9  1  0  3 11   3
	 *     4    4  4  4  4  4  4    6  7  8  9 10 11            14 13 12   15 16 17            29 15  7  4  8 19   4
	 *     5    5  5  5  5  5  5    6  7  8  9 10 11               14 13   16 17               32 25 18 12 20 26   5
	 *
	 *          0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5    0  1  2  3  4  5
	 * </pre>
	 */
	@Test
	void testLineAddresses() {
		var fieldDescriptor = new BoardImpl(6).fieldDescriptor(3, 2);
		var fieldAddress = fieldDescriptor.fieldAddress();
		assertThat(fieldAddress)
				.isEqualTo(new FieldAddress(2, 3, 2));
		assertThat(fieldDescriptor.lineAddresses())
				.containsOnly(
						Map.entry(Direction.HORIZONTAL, new LineAddress(2, 3, fieldAddress)),
						Map.entry(Direction.VERTICAL, new LineAddress(9, 2, fieldAddress)),
						Map.entry(Direction.DOWNHILL, new LineAddress(12, 2, fieldAddress)),
						Map.entry(Direction.UPHILL, new LineAddress(16, 3, fieldAddress)));
	}

	@Test
	void fieldIndexBuildFromCenter() {
		var board = new BoardImpl(6);
		var output = new StringBuilder();
		for (var row = 0; row < board.height(); ++row) {
			var line = new StringJoiner(" ");
			for (var column = 0; column < board.width(); ++column) {
				var index = board.fieldDescriptor(column, row).fieldAddress().fieldIndex();
				line.add(String.format("%2d", index));
			}
			output.append(line.toString()).append("\n");
		}
		assertThat(output.toString())
				.isEqualTo("""
						35 33 27 23 30 34
						31 22 14 10 16 24
						28 13  5  2  6 17
						21  9  1  0  3 11
						29 15  7  4  8 19
						32 25 18 12 20 26
						""");
	}
}
