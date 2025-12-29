package cz.doleckovi.piskvorky.gtp.command;

import java.util.StringJoiner;

public class ExceptionCommand implements Command {

	private final String message;
    private final Exception cause;

    public ExceptionCommand(String message, Exception cause) {
		this.message = message;
        this.cause = cause;
    }

    @Override
    public String call() {
        throw new CommandException(this, message, cause);
    }

	@Override
	public String toString() {
		return new StringJoiner(", ", ExceptionCommand.class.getSimpleName() + "[", "]")
				.add("message='" + message + "'")
				.add("cause=" + cause)
				.toString();
	}
}
