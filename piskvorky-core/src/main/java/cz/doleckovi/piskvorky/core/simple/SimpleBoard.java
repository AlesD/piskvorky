package cz.doleckovi.piskvorky.core.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.CellAddress;
import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.board.LineDescriptor;

class SimpleBoard implements Board {

	int size;
	List<List<FieldAddress>> rows; // TODO single dimension
	List<LineDescriptor> lines;

	SimpleBoard(int size) {
		if (size < Constants.SIZE)
			throw new IllegalArgumentException("size");
		var descriptors = describeLines(size);
		var rows = List.copyOf(rows(size, descriptors));
		var lines = List.of(lines(descriptors, rows));
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
	public LineDescriptor line(int index) throws IndexOutOfBoundsException{
		return lines.get(index);
	}

    @Override
    public FieldAddress field(int index) throws IndexOutOfBoundsException {
        return null; // TODO
    }

    @Override
	public FieldAddress field(int column, int row) throws IndexOutOfBoundsException {
		return rows.get(row).get(column);
	}

	@Override
	public Collection<? extends CellAddress> cells(FieldAddress field) {
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

    private record LineDescriptorData(Direction direction, int column, int row, int length, int columnStep, int rowStep) {}

	private static List<LineDescriptorData> describeLines(int size) {
		var horizontal = new LinkedList<LineDescriptorData>();
		var vertical = new LinkedList<LineDescriptorData>();
		var uphillBelowDiagonal = new LinkedList<LineDescriptorData>();
		var uphillAboveDiagonal = new LinkedList<LineDescriptorData>();
		var downhillBellowDiagonal = new LinkedList<LineDescriptorData>();
		var downhillAboveDiagonal = new LinkedList<LineDescriptorData>();
		var lastRow = size - 1;
		for (int index = 0; index < size; ++index) {
			horizontal.add(new LineDescriptorData(Direction.HORIZONTAL, 0, index, size, 1, 0));
			vertical.add(new LineDescriptorData(Direction.VERTICAL, index, 0, size, 0, 1));
			var length = size - index;
			uphillBelowDiagonal.add(new LineDescriptorData(Direction.UPHILL, index, lastRow, length, 1, -1));
			downhillBellowDiagonal.add(new LineDescriptorData(Direction.DOWNHILL, 0, index, length, 1, 1));
			if (index > 0) {
				uphillAboveDiagonal.add(new LineDescriptorData(Direction.UPHILL, 0, lastRow - index, length, 1, -1));
				downhillAboveDiagonal.add(new LineDescriptorData(Direction.DOWNHILL, index, 0, length, 1, 1));
			}
		}
		return Stream.of(horizontal, vertical,
						uphillAboveDiagonal.reversed(), uphillBelowDiagonal,
						downhillAboveDiagonal.reversed(), downhillBellowDiagonal)
				.flatMap(List::stream).toList();
	}

	private static List<List<SimpleFieldAddress>> rows(int size, List<LineDescriptorData> lines) {
		var fields = new SimpleFieldAddress[size][size];
		var cells = new HashMap<Integer, Map<Integer, Map<Direction, SimpleCellAddress>>>(size);
		int lineIndex = 0;
		for(var line : lines) {
			var column = line.column;
			var row = line.row;
			for (int offset = 0; offset < line.length; ++offset) {
				var map = cells
						.computeIfAbsent(row, key -> new HashMap<>(size))
						.computeIfAbsent(column, key -> new EnumMap<>(Direction.class));
				map.put(line.direction, new SimpleCellAddress(lineIndex, offset));
				if (map.size() == Direction.values().length) {
					fields[row][column] = new SimpleFieldAddress(column + size * row, column, row, map);
				}
				column += line.columnStep;
				row += line.rowStep;
			}
			++lineIndex;
		}
		var result = new ArrayList<List<SimpleFieldAddress>>(size);
		for (int row = 0; row < size; ++row)
			result.add(List.of(fields[row]));
		return result;
	}

	private static List<SimpleLineDescriptor> lines(List<LineDescriptorData> descriptors, List<List<SimpleFieldAddress>> rows) {
		var result = new ArrayList<SimpleLineDescriptor>(descriptors.size());
		for (var descriptor : descriptors) {
			var addresses = new SimpleFieldAddress[descriptor.length];
			var column = descriptor.column;
			var row = descriptor.row;
			for (int offset = 0; offset < descriptor.length; ++offset) {
				addresses[offset] = rows.get(row).get(column);
				column += descriptor.columnStep;
				row += descriptor.rowStep;
			}
			result.add(new SimpleLineDescriptor(result.size(), descriptor.direction, List.of(addresses)));
		}
		return result;
	}

}
