package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.*;

import java.util.*;

public class SimpleFactory {

	record SimpleCellAddress(int size, int line, int offset) implements CellAddress {}

	public static Board board(int size) {
	}


}
