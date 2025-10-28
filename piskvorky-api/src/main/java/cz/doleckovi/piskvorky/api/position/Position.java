package cz.doleckovi.piskvorky.api.position;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.*;
import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.api.search.Move;
import cz.doleckovi.piskvorky.api.search.Search;
import cz.doleckovi.piskvorky.api.Muttable;

/** Position on board.
 * <p>Position must be thread-safe.</p>
 */
public interface Position<C extends Cell, F extends Field> {

	/** Gets board on which is the game played.
	 * @return Board instance
	 */
	Board board();

	/** Checks if the position is terminal.
	 * @return {@code true} if position contains {@value Constants#SIZE} same stones in row
	 */
	boolean isTerminal();

	Line<C> line(int index);

	F field(FieldAddress address);

	/** Creates new position with given stone on given field.
	 * <p>This method is used by {@link Game}. {@link Muttable} positions can return itself.</p>
	 * @param field Field address
	 * @param stone Stone to place
	 * @return New position with the stone on it
	 * @throws IndexOutOfBoundsException if the field is out of board
	 * @throws IllegalArgumentException  if there is already stone on the given field
	 * @throws IllegalStateException     if invoked on terminal position
	 */
	Position <C, F> withStone(FieldAddress field, Stone stone)
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
	default Position<C, F> afterMove(Move move)
			throws IndexOutOfBoundsException, IllegalArgumentException, IllegalStateException
	{
		return withStone(move, move.side().stone);
	}

}
