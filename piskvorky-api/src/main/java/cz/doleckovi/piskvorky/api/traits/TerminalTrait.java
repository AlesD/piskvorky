package cz.doleckovi.piskvorky.api.traits;

/** Terminal trait.
 * <p>Implemented by objects that have notion of <strong>terminal state</strong>.</p> */
public interface TerminalTrait {

	/** Check terminal state.
	 * @return {@code true} if object is in terminal state
	 */
	boolean isTerminal();
}
