package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.CellAddress;
import cz.doleckovi.piskvorky.api.board.Direction;

import java.util.*;
import java.util.stream.Stream;

import static cz.doleckovi.piskvorky.api.Constants.DIRECTION_COUNT;

public class BoardImpl implements Board {

	private final int size;
	private final List<FieldAddressImpl> fields;
	private final List<LineDescriptorImpl> lines;

	public BoardImpl(int size) {
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
    public FieldAddressImpl field(int column, int row) throws IndexOutOfBoundsException {
        return fields.get(column + row * size);
    }

    @Override
    public LineDescriptorImpl line(CellAddress cell) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (cell instanceof CellAddressImpl cellAddressImpl)
            return lines.get(cellAddressImpl.line());
        throw new IllegalArgumentException("Unsupported cell address");
    }

    /** Gets number of lines on the board.
     * @return Number of lines
     */
	public int lineCount() {
		return lines.size();
	}

	public LineDescriptorImpl line(int index) throws IndexOutOfBoundsException{
		return lines.get(index);
	}

    public int fieldCount() {
        return fields.size();
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

	private static List<FieldAddressImpl> fields(int size, List<LineDescriptor_> lines) {
		var fields = new FieldAddressImpl[size * size];
		var cells = new HashMap<Integer, Map<Integer, Map<Direction, CellAddressImpl>>>(size);
		int lineIndex = 0;
		for(var line : lines) {
			var column = line.column;
			var row = line.row;
			for (int offset = 0; offset < line.length; ++offset) {
				var map = cells
						.computeIfAbsent(row, _ -> new HashMap<>(size))
						.computeIfAbsent(column, _ -> new EnumMap<>(Direction.class));
				map.put(line.direction, new CellAddressImpl(lineIndex, offset));
				if (map.size() == DIRECTION_COUNT) {
					fields[column + row * size] = new FieldAddressImpl(column + size * row, column, row, map);
				}
				column += line.columnStep;
				row += line.rowStep;
			}
			++lineIndex;
		}
		return List.of(fields);
	}

	private static List<LineDescriptorImpl> lines(int size, List<LineDescriptor_> descriptors, List<FieldAddressImpl> fields) {
		var result = new ArrayList<LineDescriptorImpl>(descriptors.size());
		for (var descriptor : descriptors) {
			var addresses = new FieldAddressImpl[descriptor.length];
			var column = descriptor.column;
			var row = descriptor.row;
			for (int offset = 0; offset < descriptor.length; ++offset) {
				addresses[offset] = fields.get(column + row * size);
				column += descriptor.columnStep;
				row += descriptor.rowStep;
			}
			result.add(new LineDescriptorImpl(descriptor.direction, List.of(addresses)));
		}
		return result;
	}

}
