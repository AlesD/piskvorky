package cz.doleckovi.piskvorky.api.evaluation;

import cz.doleckovi.piskvorky.api.Self;
import cz.doleckovi.piskvorky.api.board.Side;

/** Evaluation score.
 * <p>There is only one score for a position and is same for both players.</p>
 * @param <T> Score type
 */
public interface Score<T extends Score<T>> extends Self<T> {

	/** Determine if the score is from terminal position.
	 * @return {@code true} if the score comes from terminal position
	 */
	boolean isTerminal();

	/** Determine if this score is <em>better</em> for given player.
	 * <p>The operation must be symmetric and transitive if used for same side. Comparing with different sides does not
	 * have to be symmetric.</p>
	 * <p>Normally are compared scores of same depth so the side to move is same.</p>
	 * @param other The other score
	 * @return {@code true} if player should prefer this score more than the other
	 * @throws NullPointerException if the other score is null
	 */
	boolean isBetterThan(T other, Side side);

	/** Better of two scores.
	 * @param other The other score
	 * @return {@code this} if the other score is {@code null} or this score is better than the other
	 */
	default T betterOf(T other, Side side) {
		return (other == null || isBetterThan(other, side)) ? self() : other;
	}

}
