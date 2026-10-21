package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.gtp.action.Action;

/**
 * Translates one GTP command line into the {@link Action} it denotes.
 */
public interface CommandParser {

	/**
	 * Parses a single command line.
	 *
	 * <p>The line comes without its terminator (the transport strips it); blank,
	 * whitespace-only and comment-only lines parse to a no-op action, malformed
	 * lines to a syntax-error action echoing any id recognized on the line
	 * (GTP spec &sect;3.3). Every line therefore yields an action - parsing never
	 * throws and never returns {@code null}.
	 *
	 * @param commandLine single line without line terminator; may be empty,
	 *                    must not contain embedded {@code '\n'}
	 * @return action for the line, never {@code null}
	 */
	Action parse(String commandLine);

}
