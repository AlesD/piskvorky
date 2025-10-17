package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Position;
import cz.doleckovi.piskvorky.api.board.Side;

import java.util.Optional;

/** Search algorithm abstraction.
 * @param <C> Context type
 * @param <P> Position type
 */
@FunctionalInterface
public interface Search<C extends SearchContext, P extends Position<P>> {

	/** Find best move.
	 * @param context Search context
	 * @param position Search start position
	 * @param side   Player to move
	 * @return Best move for given player - empty if position is terminal or there are no moves left
	 * @throws InterruptedException if the search was interrupted
	 */
	Optional<Move> search(C context, P position, Side side)
			throws IllegalArgumentException, IllegalStateException, InterruptedException;

}
