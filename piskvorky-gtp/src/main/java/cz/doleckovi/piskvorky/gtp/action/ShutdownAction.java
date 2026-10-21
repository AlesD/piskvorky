package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;

/** Action used to terminate processing of GTP session.
 * <p>Returns {@link Result.Ignored} result. The termination is caused by the
 * {@link Shutdown} trait it carries: accepted-and-last at every stage it flows
 * through, its {@link Canceling} inheritance winding down a cancelable command
 * still running. Used for natural EOF, broken input or a host-side shutdown.</p>
 */
public record ShutdownAction() implements Action, Shutdown {

	@Override
	public Result execute(Context context) {
		return Result.IGNORED;
	}

}
