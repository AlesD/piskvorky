package cz.doleckovi.piskvorky.api.position;

/** Position data. */
public interface PositionData {

	/** Checks if the line is terminal.
	 * @return {@code false} if position contains sequence of stones
	 */
	boolean isTerminal();

}
