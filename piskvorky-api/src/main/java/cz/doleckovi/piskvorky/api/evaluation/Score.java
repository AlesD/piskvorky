package cz.doleckovi.piskvorky.api.evaluation;

import cz.doleckovi.piskvorky.api.Self;
import cz.doleckovi.piskvorky.api.position.Side;

/** Evaluation score.
 * <p>There is only one score for a position and is same for both players.</p>
 * <p>Negamax algorithm requires <em>negation</em> operation so it can simply flip sign and clear distinction between
 * minimizing and maximizing. This works fine with primitive values or mutable instances. If the score is immutable
 * object then negation would require allocation new score instance. To overcome this we pass side to comparison
 * function.</p>
 * @param <T> Score type
 */
public interface Score<T extends Score<T>> extends Self<T> {

//	/** Determine if the score is from terminal position.
//	 * @return {@code true} if the score comes from terminal position
//	 */
//	boolean isTerminal();

	/** Determine if this score is <em>better</em> than the other from point of vie of given side.
	 * <p>All score implementations should satisfy following requirements:</p>
	 * <ul>
	 *   <li>If <em>S1 equals S2</em> then both <em>S1 is better than S2</em> and <em>S2 is better than S1</em>
	 *   must return <code>false</code> for <strong>any</strong> side.</li>
	 *   <li>If <em>S1 is better than S2</em> then <em>S2 is better than S1</em> must be <code>false</code>
	 *   for the <strong>same</strong> side.</li>
	 *   <li>If <em>S1 is better then S2</em> and <em>S2 is better than S3</em> then <em>S1 is better then S3</em> must
	 *   be also <code>true</code>.</li>
	 * </ul>
	 * @param other The other score
	 * @param side Side performing the comparison
	 * @return {@code true} if player should prefer this score more than the other
	 * @throws NullPointerException if the other score is null
	 */
	boolean isBetterThan(T other, Side side);

	/** Better of two scores.
	 * @param other The other score
	 * @param side Side performing the operation
	 * @return {@code this} if the other score is {@code null} or this score is better than the other
	 */
	default T betterOf(T other, Side side) {
		return (other == null || isBetterThan(other, side)) ? self() : other;
	}

}
