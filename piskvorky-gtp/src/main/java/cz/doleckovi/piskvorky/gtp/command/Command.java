package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.session.Session;
import cz.doleckovi.piskvorky.gtp.trait.Trait;

/** Parsed GTP command.
 * <p>Command executes work a parsed command line denotes and produces the
 * answer text. Commands can access and mutate GTP {@link Session} through
 * {@link Context}. Distinct from an {@link Action} by origin — every command
 * becomes an action, while not all actions originate from a commands.</p>
 *
 * <p>Commands can be marked with execution {@link Trait traits} which are
 * copied onward to actions that wraps them and results they produce.</p>
 *
 * <p>Commands can be reused. They must be thread safe and can assume that
 * context is not modified while they execute. They should not cache anything
 * received from context and treat all arguments as unmodifiable.</p>
 */
@FunctionalInterface
public interface Command {

	/** Executes the command.
	 * @param context Access to session state
	 * @return Answer text, {@code null} for no answer
	 * @throws CommandException a for execution exception
	 */
	String execute(Context context) throws CommandException;

}
