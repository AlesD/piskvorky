package cz.doleckovi.piskvorky.core.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.core.evaluator.Pattern;

public final class BoardImpl implements Board {

	/** Generates mapping from FieldAddress to filedIndex.
	 *
	 * <p>Normally it could be easily computed as <code>column + row * size</code>, but this
	 * method places zero index to middle of board and then fills other outwards. Fields
	 * closer to board center therefore have lower indexes than fields on edges and in corners.
	 * This aids in move ordering and filling the board.</p>
	 *
	 * @param size Board size
	 * @return List of field addresses
	 */
	static List<FieldAddress> generateFieldAddresses(int size) {
		var result = new ArrayList<FieldAddress>(size * size);
		var fieldIndexes = new int[size][size];
		for (int row = 0; row < size; ++row)
			for (int col = 0; col < size; ++col)
				fieldIndexes[row][col] = -1;
		var queue = new LinkedList<FieldAddress>();
		int fieldIndex = 0;
		queue.add(new FieldAddress(fieldIndex++, size / 2, size / 2));
		while (!queue.isEmpty()) {
			var address = queue.remove();
			if (fieldIndexes[address.row()][address.column()] == -1) {
				fieldIndexes[address.row()][address.column()] = result.size();
				result.add(address);
				if (address.row() > 0)
					queue.add(new FieldAddress(fieldIndex++, address.row() - 1, address.column()));
				if (address.column() > 0)
					queue.add(new FieldAddress(fieldIndex++, address.row(), address.column() - 1));
				if (address.row() < size - 1)
					queue.add(new FieldAddress(fieldIndex++, address.row() + 1, address.column()));
				if (address.column() < size - 1)
					queue.add(new FieldAddress(fieldIndex++, address.row(), address.column() + 1));
			}
		}
		assert Arrays.stream(fieldIndexes).flatMapToInt(Arrays::stream).allMatch(index -> index != -1);
		return result;
	}

	static List<FieldDescriptor> generateFieldDescriptors(int size, List<FieldAddress> fieldAddresses) {
		var diagonalCount = 2 * (size - Pattern.LENGTH) + 1;
		var mainDiagonalOffset = diagonalCount / 2;
		var firstDownhillLineIndex = size + size;
		var firstUphillLineIndex = firstDownhillLineIndex + diagonalCount;
		var mainDownhillDiagonalIndex = firstDownhillLineIndex + mainDiagonalOffset;
		var mainUphillDiagonalIndex = firstUphillLineIndex + mainDiagonalOffset;
		var result = new ArrayList<FieldDescriptor>();
		for (var fieldAddress : fieldAddresses) {
			int row = fieldAddress.row();
			int column = fieldAddress.column();
			var lineAddresses = new EnumMap<Direction, LineAddress>(Direction.class);
			lineAddresses.put(Direction.HORIZONTAL, new LineAddress(row, column, fieldAddress));
			lineAddresses.put(Direction.VERTICAL, new LineAddress(size + column, row, fieldAddress));
			var diagonalIndexOffset = row - column;
			int length;
			int offset;
			if (diagonalIndexOffset < 0) {
				// above main diagonal
				length = size + diagonalIndexOffset;
				offset = row;
			} else {
				// main diagonal and bellow
				length = size - diagonalIndexOffset;
				offset = column;
			}
			if (length >= Pattern.LENGTH) {
				var lineIndex = mainDownhillDiagonalIndex + diagonalIndexOffset;
				lineAddresses.put(Direction.DOWNHILL, new LineAddress(lineIndex, offset, fieldAddress));
			}
			var invertedRow = size - 1 - row;
			diagonalIndexOffset = column - invertedRow;
			if (diagonalIndexOffset < 0) {
				// above main diagonal
				length = size + diagonalIndexOffset;
				offset = column;
			} else {
				length = size - diagonalIndexOffset;
				offset = invertedRow;
			}
			if (length >= Pattern.LENGTH) {
				var lineIndex = mainUphillDiagonalIndex + diagonalIndexOffset;
				lineAddresses.put(Direction.UPHILL, new LineAddress(lineIndex, offset, fieldAddress));
			}
			result.add(new FieldDescriptor(fieldAddress, Collections.unmodifiableMap(lineAddresses)));
		}
		assert result.size() == size * size;
		return result;
	}

	static List<LineDescriptor> generateLineDescriptors(int size, List<FieldDescriptor> fieldDescriptors) {
		var maxLineOffset = new int[6 * size - 4 * Pattern.LENGTH + 2];
		Arrays.fill(maxLineOffset, -1);
		var directions = new Direction[maxLineOffset.length];
		for (var fieldDescriptor : fieldDescriptors)
			for (var entry : fieldDescriptor.lineAddresses().entrySet()) {
				var lineAddress = entry.getValue();
				var lineIndex = lineAddress.lineIndex();
				var offset = lineAddress.offset();
				if (offset > maxLineOffset[lineIndex]) {
					maxLineOffset[lineIndex] = offset;
					if (directions[lineIndex] == null)
						directions[lineIndex] = entry.getKey();
					else
						assert directions[lineIndex] == entry.getKey();
				}
			}
		var result = new ArrayList<LineDescriptor>(maxLineOffset.length);
		while (result.size() < maxLineOffset.length) {
			var lineIndex = result.size();
			var offset = maxLineOffset[lineIndex];
			assert offset != -1;
			result.add(new LineDescriptor(lineIndex, offset + 1, directions[lineIndex]));
		}
		return result;
	}

	private final int size;
	private final List<FieldDescriptor> fieldDescriptors;
	private final List<LineDescriptor> lineDescriptors;
	private final FieldDescriptor[][] fieldDescriptorArray;
	private final PositionImpl initialPosition;


	/** Create new board instance.
	 * @param size Board size
	 */
	public BoardImpl(int size) {
		if (size < Pattern.LENGTH)
			throw new IllegalArgumentException("Minimal size of the board is " + Pattern.LENGTH);

		this.size = size;
		this.fieldDescriptors = generateFieldDescriptors(size, generateFieldAddresses(size));
		this.fieldDescriptorArray = new FieldDescriptor[size][size];
		this.lineDescriptors = List.copyOf(generateLineDescriptors(size, fieldDescriptors));

		for (var fieldDescriptor : fieldDescriptors) {
			var fieldAddress = fieldDescriptor.fieldAddress();
			fieldDescriptorArray[fieldAddress.row()][fieldAddress.column()] = fieldDescriptor;
		}

		var row = new Field[size];
		Arrays.fill(row, Field.EMPTY);
		var fields = new Field[size][];
		Arrays.fill(fields, row);
		var lines = new LineImpl[lineDescriptors.size()];
		Map<Integer, LineImpl> lineCache = HashMap.newHashMap(size);
		for (int lineIndex = 0; lineIndex < lineDescriptors.size(); ++lineIndex)
			lines[lineIndex] = lineCache.computeIfAbsent(lineDescriptors.get(lineIndex).length(), LineImpl::new);
		initialPosition = new PositionImpl(this, fields, lines, false);
	}

	@Override
	public int getWidth() {
		return size;
	}

	@Override
	public int getHeight() {
		return size;
	}

	public PositionImpl initialPosition() {
		return initialPosition;
	}

	public FieldDescriptor fieldDescriptor(int column, int row) {
		return fieldDescriptorArray[row][column];
	}

	public Collection<LineAddress> lineAddresses(int column, int row) {
		return fieldDescriptorArray[row][column].lineAddresses().values();
	}

	@Deprecated
	public FieldDescriptor fieldDescriptor(FieldAddress fieldAddress) {
		return fieldDescriptors.get(fieldAddress.fieldIndex());
	}

	@Deprecated
	public List<FieldDescriptor> fieldDescriptors() {
		return fieldDescriptors;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof BoardImpl that) {
			return size == that.size;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return size;
	}

	@Override
	public String toString() {
		return "Board[" + size + ']';
	}
}
