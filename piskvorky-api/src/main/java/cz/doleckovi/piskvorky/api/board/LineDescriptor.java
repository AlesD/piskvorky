package cz.doleckovi.piskvorky.api.board;


public interface LineDescriptor extends BoardObject {
	Direction direction();
	int length();
	FieldAddress field(int offset);
}
