package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Position;

import java.util.Optional;

@FunctionalInterface
public interface Search<P extends Position<P>> {

	/** Find best move.
	 * @param position Search start position
	 * @param player   Player to move
	 * @param depth    Depth of the search
	 * @return Best move for given player - empty if position is terminal or there are no moves left
	 * @throws IllegalArgumentException for zero or less depth
	 * @throws InterruptedException     if the search was interrupted
	 */
	Optional<Move> search(P position, Player player, int depth)
			throws IllegalArgumentException, IllegalStateException, InterruptedException;

}
