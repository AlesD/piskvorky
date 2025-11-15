package cz.doleckovi.piskvorky.gtp.command;

public class NoOpAction implements Action {

	public static final NoOpAction INSTANCE = new NoOpAction();

	protected NoOpAction() {
		// Do nothing
	}

	@Override
	public void execute() throws CommandException {
		// Do nothing
	}

	@Override
	public String toString() {
		return "NoOpAction";
	}
}
