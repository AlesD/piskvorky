package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Board;
import cz.doleckovi.piskvorky.api.position.Cell;
import cz.doleckovi.piskvorky.api.position.Field;
import cz.doleckovi.piskvorky.api.position.Line;
import cz.doleckovi.piskvorky.api.position.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class SimplePosition<C extends Cell, F extends Field> implements Constants, Position<C, F> {

	private final Board board;
	private final List<Line<C>> lines;
	private final boolean terminal;

	SimplePosition(Board board) {
		this.board = board;

	}

	private SimplePosition(Board board, boolean terminal, IntFunction<Line<C>> lines_) {
		this.board = board;
		this.terminal = terminal;
		var lines = new ArrayList<Line<C>>();
		for (int index : board.l)

		this.lines = List.copyOf(lines);
	}

	@Override
	public Board board() {
		return board;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public List<SimpleLine> lines() {
		return lines;
	}

}
