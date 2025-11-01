package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.position.LineCallback;
import cz.doleckovi.piskvorky.api.position.LineData;

public class SimpleLineData implements LineData {

	static final SimpleLineData EMPTY = new SimpleLineData(new int[Board.minSize()], new int[Board.minSize()]);

	private final boolean terminal;
	private final int[] white;
	private final int[] black;

	private SimpleLineData(int[] white, int[] black) {
		terminal = white[Board.minSize()] != 0 || black[Board.minSize()] != 0;
		this.white = white;
		this.black = black;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	Updater updater(LineCallback<SimpleLineData, SimpleCellData> callback) {
		return new Updater(white.clone(), black.clone(), callback);
	}

	static class Updater {

		final int[] white;
		final int[] black;
		final LineCallback<SimpleLineData, SimpleCellData> callback;

		private Updater(int[] white, int[] black, LineCallback<SimpleLineData, SimpleCellData> callback) {
			this.white = white;
			this.black = black;
			this.callback = callback;
		}

		public SimpleCellData cellDataChanged(int offset, SimpleCellData oldData, SimpleCellData newData) {
			switch (oldData.stone()) {
				case WHITE:
					--white[oldData.white()];
					break;
				case BLACK:
					--black[oldData.black()];
					break;
			}
			switch (newData.stone()) {
				case WHITE:
					++white[newData.white()];
					break;
				case BLACK:
					++black[newData.black()];
					break;
			}
			callback.cellDataChanged(offset, oldData, newData);
			return newData;
		}

		SimpleLineData newData() {
			return new SimpleLineData(white, black);
		}

	}

}
