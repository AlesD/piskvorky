package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.gtp.trait.Canceling;

import cz.doleckovi.piskvorky.gtp.trait.Cancelable;
import cz.doleckovi.piskvorky.gtp.action.Result.Ignored;

/** Action used for interrupt line ({@code # interrupt}) and {@code SIGINT}
 * handling.
 * <p>Returns {@link Ignored} result. Carries {@link Canceling} trait and
 * therefore cancels execution of previous {@link Cancelable} actions which
 * were not yet fully executed.</p>
 */
public class InterruptAction implements Canceling, Action {

	/** Shared stateless instance. */
	public static final Action INSTANCE = new InterruptAction();

	public InterruptAction() {}

	@Override
	public Result execute(Context context) {
		return Result.IGNORED;
	}

	@Override
	public String toString() {
		return InterruptAction.class.getSimpleName();
	}

}
