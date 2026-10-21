package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.session.Session;

/** Unit of execution.
 * <p>Actions are executed sequentially by a single thread with {@link Context}
 * which gives them access to {@link Session} and produce non-null {@link Result}.
 * Failures are {@link Result.Failure} values, never thrown; see the
 * <a href="package-info.java">package contract</a>.
 */
@FunctionalInterface
public interface Action {

	/** Runs the action.
	 * @param context Access to session state
	 * @return Action result
	 */
	Result execute(Context context);

	/** Command line ID to echo in the response.
	 * <p>The ID belongs to the parsed line, not to a {@link Command} instance.
	 * This allows singleton commands that can by used repeatedly. Actions that
	 * do not come from a command line (interrupt, terminal markers) don't have
	 * IDs.</p>
	 *
	 * @return ID of the originating line, {@code null} for none
	 */
	default String id() {
		return null;
	}

}
