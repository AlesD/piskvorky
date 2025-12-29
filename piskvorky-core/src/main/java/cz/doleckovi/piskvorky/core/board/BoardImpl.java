package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.*;

import java.util.*;
import java.util.stream.Stream;

import static cz.doleckovi.piskvorky.api.Constants.DIRECTION_COUNT;

class BoardImpl implements Board {

	private final int size;
	private final List<FieldAddress> fields;
	private final List<LineDescriptor> lines;

	BoardImpl(int size) {
		if (size < Constants.WIN_LENGTH)
			throw new IllegalArgumentException("size: %d < %d".formatted(size, Constants.WIN_LENGTH));
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
    public int fieldCount() {
        return fields.size();
    }

    @Override
    public int lineCount() {
        return lines.size();
    }

    public cz.doleckovi.piskvorky.api.board.FieldAddress field(int index) {
        return fields.get(index);
    }

    @Override
    public cz.doleckovi.piskvorky.api.board.FieldAddress field(int column, int row) throws IndexOutOfBoundsException {
        return fields.get(column + row * size);
    }

	public cz.doleckovi.piskvorky.api.board.LineDescriptor line(int index) throws IndexOutOfBoundsException{
		return lines.get(index);
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof BoardImpl that) {
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
        return new StringJoiner(", ", BoardImpl.class.getSimpleName() + "[", "]")
                .add("size=" + size)
                .toString();
    }

    private record LineDescriptor_(Direction direction, int column, int row, int length, int columnStep, int rowStep) {}

	private static List<LineDescriptor_> describeLines(int size) {
		var horizontal = new LinkedList<LineDescriptor_>();
		var vertical = new LinkedList<LineDescriptor_>();
		var uphillBelowDiagonal = new LinkedList<LineDescriptor_>();
		var uphillAboveDiagonal = new LinkedList<LineDescriptor_>();
		var downhillBellowDiagonal = new LinkedList<LineDescriptor_>();
		var downhillAboveDiagonal = new LinkedList<LineDescriptor_>();
		var lastRow = size - 1;
		for (int index = 0; index < size; ++index) {
			horizontal.add(new LineDescriptor_(Direction.HORIZONTAL, 0, index, size, 1, 0));
			vertical.add(new LineDescriptor_(Direction.VERTICAL, index, 0, size, 0, 1));
			var length = size - index;
			uphillBelowDiagonal.add(new LineDescriptor_(Direction.UPHILL, index, lastRow, length, 1, -1));
			downhillBellowDiagonal.add(new LineDescriptor_(Direction.DOWNHILL, 0, index, length, 1, 1));
			if (index > 0) {
				uphillAboveDiagonal.add(new LineDescriptor_(Direction.UPHILL, 0, lastRow - index, length, 1, -1));
				downhillAboveDiagonal.add(new LineDescriptor_(Direction.DOWNHILL, index, 0, length, 1, 1));
			}
		}
		return Stream.of(horizontal, vertical,
						uphillAboveDiagonal.reversed(), uphillBelowDiagonal,
						downhillAboveDiagonal.reversed(), downhillBellowDiagonal)
				.flatMap(List::stream).toList();
	}

	private static List<FieldAddress> fields(int size, List<LineDescriptor_> lines) {
		var fields = new FieldAddress[size * size];
		var cells = new HashMap<Integer, Map<Integer, Map<Direction, CellAddress>>>(size);
		int lineIndex = 0;
		for(var line : lines) {
			var column = line.column;
			var row = line.row;
			for (int offset = 0; offset < line.length; ++offset) {
				var map = cells
						.computeIfAbsent(row, _ -> new HashMap<>(size))
						.computeIfAbsent(column, _ -> new EnumMap<>(Direction.class));
				map.put(line.direction, new CellAddress(lineIndex, offset));
				if (map.size() == DIRECTION_COUNT) {
					fields[column + row * size] = new FieldAddress(column + size * row, column, row, map);
				}
				column += line.columnStep;
				row += line.rowStep;
			}
			++lineIndex;
		}
		return List.of(fields);
	}

	private static List<LineDescriptor> lines(int size, List<LineDescriptor_> descriptors, List<FieldAddress> fields) {
		var result = new ArrayList<LineDescriptor>(descriptors.size());
        for (var index = 0; index < descriptors.size(); ++index) {
		    var descriptor = descriptors.get(index);
			var addresses = new FieldAddress[descriptor.length];
			var column = descriptor.column;
			var row = descriptor.row;
			for (int offset = 0; offset < descriptor.length; ++offset) {
				addresses[offset] = fields.get(column + row * size);
				column += descriptor.columnStep;
				row += descriptor.rowStep;
			}
			result.add(new LineDescriptor(index, descriptor.direction, List.of(addresses)));
		}
		return result;
	}

}
