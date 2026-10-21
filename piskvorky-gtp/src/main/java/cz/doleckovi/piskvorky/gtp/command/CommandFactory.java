package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import cz.doleckovi.piskvorky.gtp.session.Session;

import java.util.Collection;
import java.util.List;

/** Creates {@link Command} instances from command name and typed arguments.
 * <p>Command factories are not aware about {@link Session} — commands reach
 * the session state through the {@link Context} of their execution.
 *
 * <p>Command factories map a command name (a case sensitive string, spec §3.2
 * and §3.3) and command arguments to command instances. It is possible to
 * return same instance for same command name and arguments. For this reason
 * arguments must be immutable.</p>
 */
public interface CommandFactory {

	/** Returns list of supported commands.
	 * @return Collection of command names
	 */
	Collection<String> supportedCommands();

	/** Creates command that takes no arguments.
	 * @param commandName Command name
	 * @return Command instance for the name, or an {@link UnknownCommand}
	 */
	Command createSimpleCommand(String commandName);

	/** Creates command that takes single numeric argument.
	 * @param commandName Command name
	 * @param arg Numeric argument
	 * @return Command instance for the name, or an {@link UnknownCommand}
	 */
	Command createNumberCommand(String commandName, int arg);

	/** Creates command that takes side as argument.
	 * @param commandName Command name
	 * @param side Side argument
	 * @return Command instance for the name, or an {@link UnknownCommand}
	 */
	Command createSideCommand(String commandName, Side side);

	/** Creates command that takes vertex as argument.
	 * @param commandName Command name
	 * @param vertex Vertex argument
	 * @return Command instance for the name, or an {@link UnknownCommand}
	 */
	Command createVertexCommand(String commandName, Vertex vertex);

	/** Creates command that takes list of moves as argument.
	 * @param commandName Command name
	 * @param moves Immutable list of moves
	 * @return Command instance for the name, or an {@link UnknownCommand}
	 */
	Command createMoveCommand(String commandName, List<Move> moves);

	/** Creates command that takes key-value pair as argument.
	 * @param commandName Command name
	 * @param key Key argument
	 * @param value Value argument
	 * @return Command instance for the name, or an {@link UnknownCommand}
	 */
	Command createKVPCommand(String commandName, String key, String value);

	/** Creates command that takes text argument.
	 * @param commandName Command name
	 * @param text Text argument
	 * @return Command instance for the name, or an {@link UnknownCommand}
	 */
	Command createTextCommand(String commandName, String text);

}
