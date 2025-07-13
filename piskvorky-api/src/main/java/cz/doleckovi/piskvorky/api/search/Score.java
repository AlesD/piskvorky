package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.Self;

/** Evaluation score.
 * <p>The score is same for any player.</p>
 * @param <SELF> Score type
 */
public interface Score<SELF extends Score<SELF>> extends Self<SELF> {

	/** Determine if the score from terminal position.
	 * @return {@code true} if the score comes from terminal position
	 */
	boolean isTerminal();

	/** Determine if the score is from draw position.
	 * @return {@code true} if the score comes from position where no moves are possible
	 */
	boolean isDraw();

	/** Determine if this score is <em>better</em> for given player.
	 * <p>This operation must be transitive but may not be symmetric for opposing players - e.g. both players may prefer
	 * same score.</p>
	 * @param other Other score
	 * @return {@code true} if player should prefer this score more than the other
	 * @throws NullPointerException if the other score is null
	 */
	boolean isBetterThan(SELF other, Player player);

	/** Better of two scores.
	 * @param other Other score
	 * @return {@code this} if the other score is {@code null} or this score is better than other
	 */
	default SELF betterOf(SELF other, Player player) {
		return (other == null || isBetterThan(other, player)) ? self() : other;
	}

}
