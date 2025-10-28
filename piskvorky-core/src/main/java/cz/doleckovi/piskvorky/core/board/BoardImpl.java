package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.board.LineDescriptor;

import java.util.*;

public class BoardImpl {

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
		var fieldIndexes = new boolean[size][size];
		var queue = new PriorityQueue<>(size * size, Comparator.comparing(FieldAddress::fieldIndex));
		queue.add(new FieldAddress(0, size / 2, size / 2));
		while (!queue.isEmpty()) {
			var address = queue.remove();
			if (address.column() >= 0 && address.column() < size && address.row() >= 0 && address.row() < size
					&& !fieldIndexes[address.row()][address.column()])
			{
				fieldIndexes[address.row()][address.column()] = true;
				result.add(new FieldAddress(result.size(), address.column(), address.row()));
				int index = address.fieldIndex() / 10 * 10;
				queue.add(new FieldAddress(index + 20, address.column() - 1, address.row()));
				queue.add(new FieldAddress(index + 21, address.column(), address.row() - 1));
				queue.add(new FieldAddress(index + 22, address.column() + 1, address.row()));
				queue.add(new FieldAddress(index + 23, address.column(), address.row() + 1));
				queue.add(new FieldAddress(index + 30, address.column() - 1, address.row() - 1));
				queue.add(new FieldAddress(index + 31, address.column() + 1, address.row() - 1));
				queue.add(new FieldAddress(index + 32, address.column() - 1, address.row() + 1));
				queue.add(new FieldAddress(index + 33, address.column() + 1, address.row() + 1));
			}
		}
		for (int row = 0; row < size; ++row)
			for (int column = 0; column < size; ++column)
				assert fieldIndexes[row][column];
		return result;
	}

	static List<FieldDescriptor> generateFieldDescriptors(int size, List<FieldAddress> fieldAddresses) {
		var diagonalCount = 2 * (size - Constants.SIZE) + 1;
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
			if (length >= Constants.SIZE) {
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
			if (length >= Constants.SIZE) {
				var lineIndex = mainUphillDiagonalIndex + diagonalIndexOffset;
				lineAddresses.put(Direction.UPHILL, new LineAddress(lineIndex, offset, fieldAddress));
			}
			result.add(new FieldDescriptor(fieldAddress, Collections.unmodifiableMap(lineAddresses)));
		}
		assert result.size() == size * size;
		return List.copyOf(result);
	}

	static List<LineDescriptor> generateLineDescriptors(int size, List<FieldDescriptor> fieldDescriptors) {
		var maxLineOffset = new int[6 * size - 4 * Constants.SIZE + 2];
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

	private final List<FieldAddress> fields;
	private final List<LineDescriptor> lines;

	/** Create new board instance.
	 * @param sideSize Board side size
	 */
	public BoardImpl(int sideSize) {
		if (sideSize < Constants.SIZE)
			throw new IllegalArgumentException(String.format("Minimal side size of the board is %d", Constants.SIZE));

		this.size = sideSize;
		this.fieldDescriptors = generateFieldDescriptors(sideSize, generateFieldAddresses(sideSize));
		this.fieldDescriptorArray = new FieldDescriptor[sideSize][sideSize];
		for (var fieldDescriptor : fieldDescriptors) {
			var fieldAddress = fieldDescriptor.fieldAddress();
			fieldDescriptorArray[fieldAddress.row()][fieldAddress.column()] = fieldDescriptor;
		}
		this.lineDescriptors = List.copyOf(generateLineDescriptors(sideSize, fieldDescriptors));

		var row = new Field[sideSize];
		Arrays.fill(row, Field.EMPTY);
		var fields = new Field[sideSize][];
		Arrays.fill(fields, row);
		var lines = new LineImpl[lineDescriptors.size()];
		Map<Integer, LineImpl> lineCache = HashMap.newHashMap(sideSize);
		for (int lineIndex = 0; lineIndex < lineDescriptors.size(); ++lineIndex)
			lines[lineIndex] = lineCache.computeIfAbsent(lineDescriptors.get(lineIndex).length(), LineImpl::new);
		initialPosition = new PositionImpl(this, fields, lines, false);
	}

	@Override
	public int width() {
		return size;
	}

	@Override
	public int height() {
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
