package cz.doleckovi.piskvorky.gtp.command;

@CommandInfo(name = "query_boardsize", description = "Find the current boardsize")
public class QueryBoardSizeCommand implements Command, SessionAction {

	@Override
	public String execute() throws CommandException {
		return Integer.toString(session().getBoard().size());
	}

}
