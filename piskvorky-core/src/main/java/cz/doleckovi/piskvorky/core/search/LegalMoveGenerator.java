package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Position;
import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.api.search.MoveGenerator;

import java.util.ArrayList;
import java.util.List;

/** Generates all legal moves.
 * <p>The moves are sorted by field index.</p>
 */
public class LegalMoveGenerator implements MoveGenerator {

	public static final LegalMoveGenerator INSTANCE = new LegalMoveGenerator();

	private LegalMoveGenerator() {
		super();
	}

	@Override
	public List<FieldAddress> generateMoves(Position position, Side side) {
		var board = position.board();
		var fieldCount = board.fieldCount();
		var result = new ArrayList<FieldAddress>(fieldCount);
		for (var fieldIndex = 0; fieldIndex < fieldCount; ++fieldIndex) {
			var field = board.field(fieldIndex);
			if (position.stone(field) == Stone.EMPTY) {
				result.add(field);
			}
		}
		return result;
	}

}
