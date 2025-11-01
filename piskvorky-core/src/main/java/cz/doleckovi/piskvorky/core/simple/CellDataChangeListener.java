package cz.doleckovi.piskvorky.core.simple;

public class CellDataChangeListener {

	SimpleLineData.Updater lineDataUpdater;
	SimpleFieldData.Updater fieldDataUpdater;

	void changed(int offset, SimpleCellData oldData, SimpleCellData newData) {
		fieldDataUpdater.cellDataChanged(offset, oldData, newData);
	}
}
