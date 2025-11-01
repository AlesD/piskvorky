package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.position.Line;
import cz.doleckovi.piskvorky.api.position.LineCallback;
import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

public class SimpleLine implements Line<SimpleLineData, SimpleCellData> {

	private final SimpleLineDescriptor descriptor;
	private final SimpleLineData lineData;
	private final SimpleCellData[] cellData;

	private SimpleLine(SimpleLineDescriptor descriptor, SimpleLineData lineData, SimpleCellData[] cellData) {
		this.descriptor = descriptor;
		this.lineData = lineData;
		this.cellData = cellData;
	}

	SimpleLine(SimpleLineDescriptor descriptor) {
		var cells = new SimpleCellData[descriptor.length()];
		Arrays.fill(cells, SimpleCellData.EMPTY);
		this(descriptor, SimpleLineData.EMPTY, cells);
	}

	SimpleLineDescriptor descriptor() {
		return descriptor;
	}

	@Override
	public SimpleLineData lineData() {
		return lineData;
	}

	@Override
	public int cellCount() {
		return cellData.length;
	}

	@Override
	public SimpleCellData cellData(int offset) {
		return cellData[offset];
	}

	@Override
	public SimpleLine withStone(int offset, Stone stone, LineCallback<SimpleLineData, SimpleCellData> callback)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		if (lineData().isTerminal())
			throw new IllegalStateException("Terminal line");
		var cells = this.cellData.clone();
		var oldData = cells[offset];
		if (oldData.stone() != Stone.EMPTY)
			throw new IllegalStateException("Cell is not empty");
		var lineUpdater = lineData.updater(callback);
		var newData = cells[offset] = lineUpdater.cellDataChanged(offset, oldData, oldData.withStone(stone));
		adjustCells(cells, newData, offset, cells.length, 1, lineUpdater);
		adjustCells(cells, newData, offset, -1, -1, lineUpdater);
		var newLineData = lineUpdater.newData();
		callback.lineDataChanged(lineData, newLineData);
		return new SimpleLine(descriptor, newLineData, cells);
	}

	private static void adjustCells(SimpleCellData[] cells, SimpleCellData lastChanged, int offset, int end, int step,
			SimpleLineData.Updater updater)
	{
		SimpleCellData oldData = null;
		while ((offset += step) != end) {
			oldData = cells[offset];
			if (oldData.stone() == lastChanged.stone()) {
				// Cell with same stone => will have same data as last changed
				cells[offset] = updater.cellDataChanged(offset, oldData, lastChanged);
			} else {
				if (oldData.stone() == Stone.EMPTY) {
					// Empty cell => needs to recalculate data according to last changed and next
					var nextOffset = offset + step;
					if (nextOffset == end) {
						// There is no next
						cells[offset] = updater.cellDataChanged(offset, oldData, oldData.nextTo(lastChanged));
						return;
					}
					var nextData = cells[nextOffset];
					lastChanged = cells[offset] = updater.cellDataChanged(offset, oldData, oldData.between(lastChanged, nextData));
					// If the next is empty it might become move candidate
					while (nextData.stone() == Stone.EMPTY) {
						var newData = nextData.asMoveCandidate(lastChanged);
						if (newData == nextData)
							// No change
							return;
						lastChanged = cells[nextOffset] = updater.cellDataChanged(nextOffset, nextData, newData);
						if ((nextOffset += step) == end)
							// No next
							return;
						nextData = cells[nextOffset];
					}
				}
				return;
			}
		}
	}

}
