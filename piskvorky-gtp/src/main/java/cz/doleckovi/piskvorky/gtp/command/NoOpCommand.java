package cz.doleckovi.piskvorky.gtp.command;

public class NoOpCommand implements Command {

	public static final NoOpCommand INSTANCE = new NoOpCommand();

	private NoOpCommand() {
		// Do nothing
	}

	@Override
	public String call() {
		// Do nothing
		return null;
	}

	@Override
	public String toString() {
		return NoOpCommand.class.getSimpleName();
	}

}
