package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;

/**
 * The {@code quit} line. Produces no output of its own - ending the conversation is
 * the job of its markers: {@link Shutdown} is terminal (accepted-and-last) and
 * {@link cz.doleckovi.piskvorky.gtp.trait.Canceling}, which winds down cancelable
 * commands queued before it. The completed drain is not waited for by the submission:
 * the owning {@link cz.doleckovi.piskvorky.gtp.session.Session} joins the stages'
 * termination hooks before it releases the transport (spec §6.3.1 quit sequence). The
 * marker, copied onto the produced {@link cz.doleckovi.piskvorky.gtp.action.Result},
 * terminalizes every stage and completes the session.
 */
@CommandInfo(name = QuitCommand.NAME, description = "Ends the application")
public class QuitCommand implements Command, Shutdown {

	public static final String NAME = "quit";

	@Override
	public String execute(Context context) {
		return null;
	}

	@Override
	public String toString() {
		return NAME;
	}
}
