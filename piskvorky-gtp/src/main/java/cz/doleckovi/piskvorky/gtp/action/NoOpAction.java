package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.gtp.trait.Trait;
import cz.doleckovi.piskvorky.gtp.session.Session;
import cz.doleckovi.piskvorky.gtp.action.Result.Ignored;

/** Action without any observable effect.
 * <p>Returns {@link Ignored} result. Does not have any {@link Trait}s.
 * Used for command lines ignored by the protocol (empty, whitespace-only,
 * comment-only).
 */
public class NoOpAction implements Action {

	/** Shared stateless instance. */
	public static final Action INSTANCE = new NoOpAction();

	public NoOpAction() {}

	@Override
	public Result execute(Context context) {
		return Result.IGNORED;
	}

	@Override
	public String toString() {
		return NoOpAction.class.getSimpleName();
	}

}
