package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.*;

import java.util.*;
import java.util.stream.Stream;

class SimpleBoard implements Board {

	static final int DIRECTION_COUNT = Direction.values().length;

	int size;
	List<SimpleFieldAddress> fields;
	List<SimpleLineDescriptor> lines;

	SimpleBoard(int size) {
		if (size < Constants.SIZE)
			throw new IllegalArgumentException("size");
		var descriptors = describeLines(size);
		this.size = size;
		this.fields = List.copyOf(fields(size, descriptors));
		this.lines = List.copyOf(lines(size, descriptors, fields));
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int lineCount() {
		return lines.size();
	}

	@Override
	public SimpleLineDescriptor line(int index) throws IndexOutOfBoundsException{
		return lines.get(index);
	}

    @Override
    public SimpleFieldAddress field(int index) throws IndexOutOfBoundsException {
        return fields.get(index);
    }

    @Override
	public SimpleFieldAddress field(int column, int row) throws IndexOutOfBoundsException {
		return fields.get(column + row * size);
	}

	@Override
	public Collection<SimpleCellAddress> cells(FieldAddress field) {
		if (field instanceof SimpleFieldAddress simpleFieldAddress) {
			return simpleFieldAddress.cells();
		}
		throw new IllegalArgumentException("field");
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof final SimpleBoard that) {
            return size == that.size;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(size);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleBoard.class.getSimpleName() + "[", "]")
                .add("size=" + size)
                .toString();
    }

    private record LineDescriptor(Direction direction, int column, int row, int length, int columnStep, int rowStep) {}

	private static List<LineDescriptor> describeLines(int size) {
		var horizontal = new LinkedList<LineDescriptor>();
		var vertical = new LinkedList<LineDescriptor>();
		var uphillBelowDiagonal = new LinkedList<LineDescriptor>();
		var uphillAboveDiagonal = new LinkedList<LineDescriptor>();
		var downhillBellowDiagonal = new LinkedList<LineDescriptor>();
		var downhillAboveDiagonal = new LinkedList<LineDescriptor>();
		var lastRow = size - 1;
		for (int index = 0; index < size; ++index) {
			horizontal.add(new LineDescriptor(Direction.HORIZONTAL, 0, index, size, 1, 0));
			vertical.add(new LineDescriptor(Direction.VERTICAL, index, 0, size, 0, 1));
			var length = size - index;
			uphillBelowDiagonal.add(new LineDescriptor(Direction.UPHILL, index, lastRow, length, 1, -1));
			downhillBellowDiagonal.add(new LineDescriptor(Direction.DOWNHILL, 0, index, length, 1, 1));
			if (index > 0) {
				uphillAboveDiagonal.add(new LineDescriptor(Direction.UPHILL, 0, lastRow - index, length, 1, -1));
				downhillAboveDiagonal.add(new LineDescriptor(Direction.DOWNHILL, index, 0, length, 1, 1));
			}
		}
		return Stream.of(horizontal, vertical,
						uphillAboveDiagonal.reversed(), uphillBelowDiagonal,
						downhillAboveDiagonal.reversed(), downhillBellowDiagonal)
				.flatMap(List::stream).toList();
	}

	private static List<SimpleFieldAddress> fields(int size, List<LineDescriptor> lines) {
		var fields = new SimpleFieldAddress[size * size];
		var cells = new HashMap<Integer, Map<Integer, Map<Direction, SimpleCellAddress>>>(size);
		int lineIndex = 0;
		for(var line : lines) {
			var column = line.column;
			var row = line.row;
			for (int offset = 0; offset < line.length; ++offset) {
				var map = cells
						.computeIfAbsent(row, _ -> new HashMap<>(size))
						.computeIfAbsent(column, _ -> new EnumMap<>(Direction.class));
				map.put(line.direction, new SimpleCellAddress(lineIndex, offset));
				if (map.size() == DIRECTION_COUNT) {
					fields[column + row * size] = new SimpleFieldAddress(column + size * row, column, row, map);
				}
				column += line.columnStep;
				row += line.rowStep;
			}
			++lineIndex;
		}
		return List.of(fields);
	}

	private static List<SimpleLineDescriptor> lines(int size, List<LineDescriptor> descriptors, List<SimpleFieldAddress> fields) {
		var result = new ArrayList<SimpleLineDescriptor>(descriptors.size());
		for (var descriptor : descriptors) {
			var addresses = new SimpleFieldAddress[descriptor.length];
			var column = descriptor.column;
			var row = descriptor.row;
			for (int offset = 0; offset < descriptor.length; ++offset) {
				addresses[offset] = fields.get(column + row * size);
				column += descriptor.columnStep;
				row += descriptor.rowStep;
			}
			result.add(new SimpleLineDescriptor(result.size(), descriptor.direction, List.of(addresses)));
		}
		return result;
	}

}
