package cz.doleckovi.piskvorky.gtp.action;

/** Creates {@link Action}s. */
public interface ActionFactory {

	/** Creates the action Go GUI interruption
	 * <p>Used either for the interrupt line ({@code # interrupt}) or for
	 * handling SIGINT.</p>
	 */
	default Action createInterruptAction() {
		return InterruptAction.INSTANCE;
	}

	/** Creates the no-op action.
	 * <p>Used for an ignored lines (empty, whitespace or comment only).</p>
	 */
	default Action createNoOpAction() {
		return NoOpAction.INSTANCE;
	}

	/** Creates action rejecting an unparsable command line.
	 * @param id      ID recognized on the malformed line, or {@code null}
	 * @param message Error text reported to the controller
	 * @return Action producing a {@code ? [ID]} response
	 */
	default Action createSyntaxErrorAction(String id, String message) {
		return new SyntaxErrorAction(id, message);
	}

	/** Creates action that will terminate processing.
	 * <p>Used to terminate execution loop.</p>
	 */
	default Action createShutdownAction(String message) {
		return new ShutdownAction();
	}
}
