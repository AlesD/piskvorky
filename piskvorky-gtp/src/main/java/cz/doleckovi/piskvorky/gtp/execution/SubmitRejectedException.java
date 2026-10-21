package cz.doleckovi.piskvorky.gtp.execution;

import java.io.Serial;

import cz.doleckovi.piskvorky.gtp.trait.Shutdown;

/** Thrown by execution loop which does not accept tasks anymore.
 * <p>Thrown by {@link ExecutionLoop#submit} of an execution loop which
 * accepted a {@link Shutdown}-carrying task, ended spontaneously or was
 * closed by its worker's final block. Rejection is the caller's only means
 * of detecting that an execution loop terminated on its own, which is why it
 * carries a type of its own: any other {@link RuntimeException} produced by
 * executor loop - stays a plain failure of the submitted work.</p>
 */
public class SubmitRejectedException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 5127394615283746152L;

	/** @param message Message from execution loop */
	public SubmitRejectedException(String message) {
		super(message);
	}

}
