package cz.doleckovi.piskvorky.gtp.command;

import java.io.Serial;

import static java.util.Objects.requireNonNull;

/** Exception for command execution issues. */
public class CommandException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 6409761567027274525L;

	/** Constructs new instance.
	 * @param message Message to use as command result
	 */
	public CommandException(String message) {
		super(requireNonNull(message, "Parameter 'message' is null"));
	}

	/** Constructs new instance.
	 * @param message Message to use as command result
	 * @param cause Cause of excecution exception
	 */
	public CommandException(String message, Throwable cause) {
		super(requireNonNull(message, "Parameter 'message' is null"), cause);
	}

}
