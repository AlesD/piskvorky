package cz.doleckovi.piskvorky.api.position;

import cz.doleckovi.piskvorky.api.Muttable;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.api.search.Move;
import cz.doleckovi.piskvorky.api.search.Search;

/** Position on board.
 * <p>Position must be thread-safe.</p>
 */
public interface Position<P extends PositionData, F extends FieldData> {

	/** Gets board on which is the game played.
	 * @return Board instance
	 */
	Board board(); // Does position really need board?

	/** Creates new position with given stone on given fieldAddress.
	 * <p>This method is used by {@link Game}. {@link Muttable} positions can return itself.</p>
	 * @param fieldAddress Field address
	 * @param stone Stone to place
	 * @return New position with the stone on it
	 * @throws IndexOutOfBoundsException if the fieldAddress is out of board
	 * @throws IllegalArgumentException  if there is already stone on the given fieldAddress
	 * @throws IllegalStateException     if invoked on terminal position
	 */
	Position <P, F> withStone(FieldAddress fieldAddress, Stone stone)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException;

	/** Creates new position by executing move.
	 * <p>This method is used by {@link Search} algorithm. Default implementation delegates to {@link #withStone}.
	 * The positions might override this method to employ caching or use additional information provided by move
	 * implementations.</p>
	 * @param move Move to execute
	 * @return New position after move execution
	 * @throws IndexOutOfBoundsException if move is out of board
	 * @throws IllegalArgumentException  if the move can't be executed
	 * @throws IllegalStateException     if executed against terminal position
	 */
	default Position<P, F> afterMove(Move move)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		return withStone(move.field(), move.side().stone);
	}

}
