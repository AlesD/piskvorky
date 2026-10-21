package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.core.GameImpl;
import cz.doleckovi.piskvorky.core.board.BoardImpl;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Test implementation of {@link Context}: board and game backed by the
 * {@code piskvorky-core} test-scope implementations, message blocks recorded for
 * assertions. The retired {@code Context.SILENT} production constant lives on here as
 * {@link #SILENT} for game-free tests - same instance for all commands, commands that
 * need nothing from it simply do not touch it.
 */
public class TestContext implements Context {

	/** Shared instance, equivalent of the former {@code Context.SILENT}. */
	public static final TestContext SILENT = new TestContext();

	private final List<String> written = new CopyOnWriteArrayList<>();
	private final LineWriter messages = written::add;

	private Board board = new BoardImpl(19);
	private Game game = new GameImpl(board);

	@Override
	public LineWriter messages() {
		return messages;
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public void setBoard(int size) {
		board = new BoardImpl(size);
		game = new GameImpl(board);
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public void resetGame() {
		game = new GameImpl(board);
	}

	/** Message blocks written so far, in order. */
	public List<String> writtenMessages() {
		return List.copyOf(written);
	}

}
