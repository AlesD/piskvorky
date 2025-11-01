package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.position.LineCallback;
import cz.doleckovi.piskvorky.api.position.PositionCallback;
import cz.doleckovi.piskvorky.api.position.PositionData;

public class SimplePositionData implements PositionData {

	public static final SimplePositionData EMPTY = new SimplePositionData(false);

	private final Boolean terminal;

	SimplePositionData(boolean terminal) {
		this.terminal = terminal;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	public Updater updater() {
		return new Updater();
	}

	static final class Updater {

		private final int fieldIndex;
		private final SimpleLineDescriptor lineDescriptor;
		private final SimpleFieldData[] fieldData;
		private final SimpleFieldData.Updater updater;
		private final PositionCallback<SimplePositionData, SimpleFieldData> callback;

		private SimplePosition.CellDataChange cellDataChange;

		public Updater(int fieldIndex, SimpleLineDescriptor lineDescriptor, SimpleFieldData[] fieldData,
				SimpleFieldData.Updater updater, PositionCallback<SimplePositionData, SimpleFieldData> callback)
		{
			this.fieldIndex = fieldIndex;
			this.lineDescriptor = lineDescriptor;
			this.fieldData = fieldData;
			this.updater = updater;
			this.callback = callback;
		}

		SimpleFieldData fieldDataChanged(int index, SimpleFieldData oldData, SimpleFieldData newData) {
			return newData;
		}

		SimpleLineData lineDataChanged(int index, SimpleLineData oldData, SimpleLineData newData) {
			return newData;
		}

		public SimplePositionData newData() {

		}
	}

}
