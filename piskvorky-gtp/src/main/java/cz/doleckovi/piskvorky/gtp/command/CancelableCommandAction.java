package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.trait.Cancelable;

/** Action for execution of {@link Command}s implementing {@link Cancelable}. */
final class CancelableCommandAction extends CommandAction implements Cancelable {

	private final Cancelable cancelable;

	/** Creates new instance.
	 * @param id         ID to echo in the response, or {@code null} for none
	 * @param command    Command to execute
	 * @param cancelable The {@code command} seen as {@link Cancelable}
	 */
	CancelableCommandAction(String id, Command command, Cancelable cancelable) {
		super(id, command);
		this.cancelable = cancelable;
	}

	@Override
	public boolean cancelDuringExecution() {
		return cancelable.cancelDuringExecution();
	}

	@Override
	public void cancelBeforeExecution() {
		cancelable.cancelBeforeExecution();
	}

}
