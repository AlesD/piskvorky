package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.position.Position;
import cz.doleckovi.piskvorky.api.position.Side;

import java.util.List;

/** Move generation interface.
 * <p>All <em>possible</em> moves are generally property of position. Search algorithm can might not be interested in
 * all moves. It might do search extension and be only interested in so called <em>killer moves</em>. The order of moves
 * also matter - some move generators might prefer <em>attacking moves</em> over <em>defending moves</em>.</p>
 * <p>The generator might cache previously generated moves and/or alter their ordering based on previous searches.</p>
 * @param <P> Position type
 */
public interface MoveGenerator<P extends Position<P>> {

	/** Generate moves for given position.
	 * <p>Order of moves matter. Search algorithm will use the moves in given order.</p>
	 * @param position Position for which should be the moves generated
	 * @param side Player for which are the moves generated
	 * @return List of moves for given player
	 */
	List<Move> generateMoves(P position, Side side);

}
