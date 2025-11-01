package cz.doleckovi.piskvorky.api.position;

import cz.doleckovi.piskvorky.api.board.FieldAddress;

public interface PositionCallback<P extends PositionData, F extends FieldData> {

	default void fieldDataChanged(FieldAddress address, F oldData, F newData) {
		// do nothing
	}

	default void positionDataChanged(P oldData, P newData) {
		// do nothing
	}

}
