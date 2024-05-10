package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Player;
import cz.doleckovi.piskvorky.api.board.Stone;

public class Position {

	final Field[][] fields;
	final Line[] lines;

	public final cz.doleckovi.piskvorky.core.board.Board board;
	public final boolean terminal;

	Position(Board board, Field[][] fields, Line[] lines, boolean terminal) {
		this.board = board;
		this.fields = fields;
		this.lines = lines;
		this.terminal = terminal;
	}

	public boolean isTerminal() {
		return terminal;
	}

	public Stone stone(int column, int row) {
		return fields[row][column].stone();
	}

	@Deprecated
	public Position withStone(FieldAddress fieldAddress, Player player) {
		return withStone(fieldAddress.column(), fieldAddress.row(), player.stone);
	}

	public Position withStone(int column, int row, Stone stone) {
		var newFields = fields.clone();
		var newLines = lines.clone();
		var newTerminal = terminal;
		var newRow = newFields[row].clone();
		newRow[column] = newRow[column].withStone(stone);
		newFields[row] = newRow;
		for (var lineAddress : board.lineAddresses(column, row)) {
			var lineIndex = lineAddress.lineIndex();
			var lineClone = lines[lineIndex].withStone(lineAddress.offset(), stone);
			newLines[lineIndex] = lineClone;
			newTerminal = newTerminal || lineClone.isTerminal();
		}
		return new Position(board, newFields, newLines, newTerminal);
	}

}
