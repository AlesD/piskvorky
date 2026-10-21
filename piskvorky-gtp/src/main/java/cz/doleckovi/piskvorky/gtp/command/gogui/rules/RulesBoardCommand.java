package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;
import cz.doleckovi.piskvorky.gtp.command.CommandInfo;

@CommandInfo(name = RulesBoardCommand.NAME, description = "Report the board in GoGui rules format")
public class RulesBoardCommand implements Command {

	public static final String NAME = "gogui-rules_board";

	@Override
	public String execute(Context context) throws CommandException {
		var position = context.getGame().position();
		var result = new StringBuilder();
		for (int row = position.board().size() - 1; row >= 0; --row) {
			for (int column = 0; column < position.board().size(); ++column) {
				var field = position.board().field(column, row);
				if (field == null) {
					result.append(" ?");
				} else {
					var stone = position.stone(field);
					result.append(switch (stone) {
						case EMPTY -> " .";
						case WHITE -> " O";
						case BLACK -> " X";
						case BLOCK -> " ?";
					});
				}
			}
			if (row > 0) result.append("\n  ");
		}
		return result.toString();
	}

	@Override
	public String toString() {
		return NAME;
	}

}
