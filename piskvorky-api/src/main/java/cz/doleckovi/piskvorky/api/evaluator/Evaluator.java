package cz.doleckovi.piskvorky.api.evaluator;

import cz.doleckovi.piskvorky.api.board.Position;


public interface Evaluator {

	Score evaluate(Position position);

}
