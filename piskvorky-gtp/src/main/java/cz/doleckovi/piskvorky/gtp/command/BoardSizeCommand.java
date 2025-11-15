package cz.doleckovi.piskvorky.gtp.command;

@CommandInfo(name = "boardsize", description = "Sets board size")
public class BoardSizeCommand implements Action, SessionAction, CancelingAction, InterruptingAction {

	private final int size;

	public BoardSizeCommand(int size) {
		this.size = size;
	}

	@Override
	public void execute() throws CommandException {
		try {
			session().setBoard(size);
		} catch (Exception e) {
			throw new CommandException(this, "unacceptable size", e);
		}
	}

}
