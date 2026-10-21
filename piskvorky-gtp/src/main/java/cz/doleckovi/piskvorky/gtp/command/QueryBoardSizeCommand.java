package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;

@CommandInfo(name = QueryBoardSizeCommand.NAME, description = "Find the current boardsize")
public class QueryBoardSizeCommand implements Command {

	public static final String NAME = "query_boardsize";

	@Override
	public String execute(Context context) throws CommandException {
		return Integer.toString(context.getBoard().size());
	}

	@Override
	public String toString() {
		return NAME;
	}

}
