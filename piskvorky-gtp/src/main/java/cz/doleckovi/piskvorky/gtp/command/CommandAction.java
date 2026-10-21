package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.action.Result.Failure;
import cz.doleckovi.piskvorky.gtp.action.Result.Success;
import cz.doleckovi.piskvorky.gtp.trait.Cancelable;
import cz.doleckovi.piskvorky.gtp.trait.Traits;

import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/** Action for {@link Command} execution.
 * <p>This action delegates to {@link Command#execute} turning its answer into a
 * {@link Success} and any exception into a {@link Failure}.
 *
 * <p>This action carries the ID from command line and passes it to result. Traits
 * declared by the command are copied onto the produced result.</p>
 */
public class CommandAction implements Action {

	private static final Logger LOG = Logger.getLogger(CommandAction.class.getName());

	/** Wraps {@code command} into an action claiming its traits.
	 * <p>{@link Cancelable} variant is returned when the command declares that
	 * capability.</p>
	 * @param id      ID to echo in the response, or {@code null} for none
	 * @param command Command to execute
	 * @return Action executing the command
	 * @throws NullPointerException if {@code command} is {@code null}
	 */
	public static Action of(String id, Command command) {
		requireNonNull(command, "Parameter 'command' is null");
		var action = command instanceof Cancelable cancelable
				? new CancelableCommandAction(id, command, cancelable)
				: new CommandAction(id, command);
		return Traits.copyTraits(command, action);
	}

	private final String id;
	private final Command command;

	/** Creates new instance.
	 * @param id      ID to echo in the response, or {@code null} for none
	 * @param command Command to execute
	 */
	public CommandAction(String id, Command command) {
		this.id = id;
		this.command = requireNonNull(command, "Parameter 'command' is null");
	}

	/** ID of the wrapped command.
	 * @return ID originating from command line, may be {@code null}
	 */
	@Override
	public String id() {
		return id;
	}

	/** The wrapped command.
	 * @return Command instance
	 */
	public Command command() {
		return command;
	}

	/** Executes the command.
	 * @param context Context to command execution
	 * @return Either {@link Success} or {@link Failure} carrying the ID
	 */
	@Override
	public Result execute(Context context) {
		Result result;
		try {
			result = new Result.Success(id, command.execute(context));
		} catch (CommandException e) {
			result = new Result.Failure(id, e.getMessage());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e, () -> "Command execution failed: " + command);
			result = new Result.Failure(id, "unknown error");
		}
		return Traits.copyTraits(command, result);
	}

	@Override
	public String toString() {
		var result = new StringJoiner(", ", getClass().getSimpleName() + "[", "]");
		if (id != null) result.add("id=" + id);
		return result.add("command=" + command).toString();
	}

}
