package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.Immutable;

public interface LineDescriptor extends Immutable {

    int index();
	Direction direction();
	int length();
	FieldAddress field(int offset);

}
