package cz.doleckovi.piskvorky.api.game;

import cz.doleckovi.piskvorky.api.board.Player;
import cz.doleckovi.piskvorky.api.board.Position;

import java.util.List;
import java.util.Optional;

/** Game. */
public interface Game {

	/** Game ID. */
	int getId();

	/** Current game position. */
	Position getPosition();

	/** Gets moves played so far.
	 * @return Moves from initial to current position
	 */
	List<? extends Move> getMoves();

	/** Last move.
	 * <p>Default implementation iterates over moves to find last move.</p>
	 * @return Last move played in the game
	 */
	default Optional<? extends Move> getLastMove() {
		var moves = getMoves().iterator();
		Move result = null;
		while (moves.hasNext())
			result = moves.next();
		return Optional.ofNullable(result);
	}

	/** Determine player to move.
	 * <p>Default implementation returns opponent of player who made last move or {@link Player#WHITE} if the last move
	 * is empty.</p>
	 * @return Player to place next stone
	 */
	default Player playerToMove() {
		return getLastMove().map(move -> move.getPlayer().opponent()).orElse(Player.WHITE);
	}

	/** Make a move in game.
	 * @param column Column of board
	 * @param row Row of the board
	 * @throws IllegalArgumentException if the target field is not empty
	 * @throws IndexOutOfBoundsException if the coordinates are out of the board
	 * @throws IllegalStateException if the game is in terminal state
	 */
	void move(int column, int row)
			throws IllegalArgumentException, IndexOutOfBoundsException, IllegalStateException;

	/** Undo last move.
	 * @throws IllegalStateException if the game is in its initial position
	 */
	void undo() throws IllegalStateException;

}
