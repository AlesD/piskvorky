package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.Self;

/** Evaluation score.
 * <p>There is only one score for a position.</p>
 * @param <SELF> Score type
 */
public interface Score<SELF extends Score<SELF>> extends Self<SELF> {

	/** Determine if the score from terminal position.
	 * @return {@code true} if the score comes from terminal position
	 */
	boolean isTerminal();

	/** Determine if this score is <em>better</em> for given player.
	 * <p>This operation must be transitive but may not be symmetric <em>for opposing players</em> which means that both
	 * players may prefer the same score.</p>
	 * @param other The other score
	 * @return {@code true} if player should prefer this score more than the other
	 * @throws NullPointerException if the other score is null
	 */
	boolean isBetterThan(SELF other, Player player);

	/** Better of two scores.
	 * @param other The other score
	 * @return {@code this} if the other score is {@code null} or this score is better than the other
	 */
	default SELF betterOf(SELF other, Player player) {
		return (other == null || isBetterThan(other, player)) ? self() : other;
	}

}
