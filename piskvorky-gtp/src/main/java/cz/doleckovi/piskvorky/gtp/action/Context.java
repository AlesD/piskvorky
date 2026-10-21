package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.execution.ActionProcessingLoop;
import cz.doleckovi.piskvorky.gtp.session.Session;
import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;

/** Command execution context.
 * <p>Contex allows {@link Command}s to access and modify internal state of
 * {@link Session} (spec &sect;5). Piskvorky is different game than Go and
 * therefore the state differs. Commands are executed serially (spec &sect;6.2)
 * by sigle thread of so the context does not need synchronization. The state
 * can change outside of command execution. Command must not keep reference
 * to context or state when they finish execution.</p>
 */
public interface Context {

	/** Line-oriented output beyond command answer.
	 * <p>Can be used for diagnostics and GoGui live graphics (spec &sect;2.8;
	 * ch. 8, see {@code docs/gogui/live-graphics.md}).</p>
	 * @return Message channel of session transport
	 * @see GtpTransport#messages()
	 */
	LineWriter messages();

	/** Current board.
	 * @return Current board used in session
	 */
	Board getBoard();

	/** Changes size of board.
	 * <p>While setting the board to same size as it currently has might not
	 * change the board instance itself, it always {@link #resetGame() resets}
	 * the game.</p>
	 * @param size Board size
	 */
	void setBoard(int size);

	/** Current game.
	 * @return Current game used in session
	 */
	Game getGame();

	/** Replaces the game with a fresh one on the current board. */
	void resetGame();

}
