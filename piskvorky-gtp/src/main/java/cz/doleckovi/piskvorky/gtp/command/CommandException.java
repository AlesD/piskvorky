package cz.doleckovi.piskvorky.gtp.command;

public class CommandException extends RuntimeException {

	private final Object source;

	public CommandException(Object source, String message) {
		this.source = source;
		super(message);
	}

	public CommandException(Object source, String message, Throwable cause) {
		this.source = source;
		super(message, cause);
	}

}
