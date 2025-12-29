package cz.doleckovi.piskvorky.gtp.command;

@CommandInfo(name = "query_boardsize", description = "Find the current boardsize")
public class QueryBoardSizeCommand implements Command, SessionAction {

	@Override
	public String call() throws CommandException {
		return Integer.toString(session().getBoard().size());
	}

	@Override
	public String toString() {
		return QueryBoardSizeCommand.class.getSimpleName() + "[]";
	}

}
