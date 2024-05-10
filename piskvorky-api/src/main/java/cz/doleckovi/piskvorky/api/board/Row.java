package cz.doleckovi.piskvorky.api.board;

public interface Row extends Generation {

	int getLength();
	Stone getStone(int index);

}
