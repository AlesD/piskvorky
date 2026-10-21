package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;

@CommandInfo(name = ClearBoardCommand.NAME, description = "Clears the board")
public class ClearBoardCommand implements Command {

	public static final String NAME = "clear_board";

	@Override
	public String execute(Context context) {
		context.resetGame();
		return null;
	}

	@Override
	public String toString() {
		return NAME;
	}

}
