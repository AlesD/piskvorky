package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;
import cz.doleckovi.piskvorky.gtp.command.CommandInfo;

import java.util.StringJoiner;

/**
 * Reports every move playable in the current position (GoGui rules query - it drives
 * what the GUI lets the user click).
 *
 * <p>Legal in piskvorky is simply every <em>empty</em> field; the enumeration is
 * deliberately owned by this rules adapter instead of the engine-side
 * {@code api.search.MoveGenerator}: a search candidate generator may legally filter
 * (e.g. near-stone fields), but the rules report must be complete and ordered by
 * field index.</p>
 */
@CommandInfo(name = RulesLegalMovesCommand.NAME, description = "Report all legal moves")
public class RulesLegalMovesCommand implements Command {

	public static final String NAME = "gogui-rules_legal_moves";

	@Override
	public String execute(Context context) throws CommandException {
		var position = context.getGame().position();
		if (position.isTerminal())
			return "";
		var board = position.board();
		var result = new StringJoiner(" ");
		for (var index = 0; index < board.fieldCount(); index++) {
			var field = board.field(index);
			if (position.stone(field) == Stone.EMPTY)
				result.add(Vertex.of(board.field(index)).toString());
		}
		return result.toString();
	}

	@Override
	public String toString() {
		return NAME;
	}

}
