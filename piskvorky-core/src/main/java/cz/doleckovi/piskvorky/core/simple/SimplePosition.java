package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.position.LineCallback;
import cz.doleckovi.piskvorky.api.position.Position;
import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.Arrays;

public class SimplePosition implements Position<SimplePositionData, SimpleFieldData> {

	private final SimpleBoard board;
	private final SimplePositionData positionData;
	private final SimpleLine[] lines;
	private final SimpleFieldData[] fieldData;

	private SimplePosition(SimpleBoard board, SimplePositionData positionData, SimpleLine[] lines, SimpleFieldData[] fieldData) {
		this.board = board;
		this.positionData = positionData;
		this.lines = lines;
		this.fieldData = fieldData;
	}

	SimplePosition(SimpleBoard board) {
		var lineCache = new SimpleLine[board.size()];
		var lines = new SimpleLine[board.lineCount()];
		for (var lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
			var descriptor = board.line(lineIndex);
			var line = lineCache[descriptor.length()];
			if (line == null) {
				line = new SimpleLine(descriptor);
				lineCache[descriptor.length()] = line;
			}
			lines[lineIndex] = line;
		}
		var fields = new SimpleFieldData[board.fieldCount()];
		Arrays.fill(fields, SimpleFieldData.EMPTY);
		this(board, SimplePositionData.EMPTY, lines, fields);
	}

	@Override
	public Board board() {
		return board;
	}

	@Override
	public SimplePosition withStone(FieldAddress fieldAddress, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		if (!(fieldAddress instanceof SimpleFieldAddress simpleFieldAddress))
			throw new IllegalArgumentException("fieldAddress");
		var lines = this.lines.clone();
		var fieldData = this.fieldData.clone();
		var terminal = false;
		// Field at line intersections will receive multiple cell data updates => use updater
		var oldFieldData = fieldData[simpleFieldAddress.index()];
		var fieldUpdater = oldFieldData.updater(stone);
		var positionUpdater = positionData.updater();
		for (var direction : Direction.values()) {
			var cellAddress = simpleFieldAddress.cell(direction);
			if (cellAddress == null)
				continue;
			var line = lines[cellAddress.line()];
			SimpleLineCallback callback = new SimpleLineCallback(cellAddress.offset(), line.descriptor(), fieldData,
					positionUpdater, fieldUpdater);
			lines[cellAddress.line()] = line.withStone(cellAddress.offset(), stone, callback);
		}
		var newFieldData = fieldUpdater.newData();
		fieldData[simpleFieldAddress.index()] = positionUpdater.fieldDataChanged(simpleFieldAddress.index(), oldFieldData, newFieldData);
		var newPositionData = positionUpdater.newData();
		return new SimplePosition(board, newPositionData, lines, fieldData);
	}

	static final class SimpleLineCallback implements LineCallback<SimpleLineData, SimpleCellData> {

		private final int offset;
		private final SimpleLineDescriptor lineDescriptor;
		private final SimpleFieldData[] fieldData;
		private final SimplePositionData.Updater positionUpdater;
		private final SimpleFieldData.Updater fieldUpdater;

		private SimpleCellData oldCellData;
		private SimpleCellData newCellData;
		private SimpleLineData newLineData;

		public SimpleLineCallback(int offset, SimpleLineDescriptor descriptor, SimpleFieldData[] fieldData,
				SimplePositionData.Updater positionUpdater, SimpleFieldData.Updater fieldUpdater)
		{
			this.offset = offset;
			this.lineDescriptor = descriptor;
			this.fieldData = fieldData;
			this.positionUpdater = positionUpdater;
			this.fieldUpdater = fieldUpdater;
		}

		@Override
		public void cellDataChanged(int offset, SimpleCellData oldData, SimpleCellData newData) {
			if (offset == this.offset) {
				// This is the file where stone is placed - it is on intersection of multiple lines => delay callback
				fieldUpdater.withCell(oldData, newData);
			} else {
				var index = lineDescriptor.field(offset).index();
				var oldFieldData = fieldData[index];
				fieldData[index] = positionUpdater.fieldDataChanged(index, oldFieldData, oldFieldData.withCell(oldData, newData));
			}
		}

		@Override
		public void lineDataChanged(SimpleLineData oldData, SimpleLineData newData) {
			newLineData = positionUpdater.lineDataChanged(lineDescriptor.index(), oldData, newData);
		}

	}

}
