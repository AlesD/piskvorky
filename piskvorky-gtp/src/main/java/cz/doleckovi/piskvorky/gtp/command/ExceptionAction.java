package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;

public class ExceptionAction implements Action {

	private final String message;
    private final RuntimeException cause;

    public ExceptionAction(String message, RuntimeException cause) {
		this.message = message;
        this.cause = cause;
    }

    @Override
    public void execute() {
        throw new CommandException(this, message, cause);
    }

}
