package cz.doleckovi.piskvorky.gtp.session;

import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.gtp.Session;
import org.springframework.beans.factory.BeanFactory;

public class SpringSession implements Session {

	private final BeanFactory beanFactory;
	private Board board;
	private Game game;

	public SpringSession(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		setBoard(19);
	}

	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public void setBoard(int size) {
		board = beanFactory.getBean(Board.class, size);
		resetGame();
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public void resetGame() {
		game = beanFactory.getBean(Game.class, board);
	}

}
