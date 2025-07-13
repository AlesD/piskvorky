package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Position;

public interface Evaluator<P extends Position<P>, S extends Score<S>> {

	S evaluate(P position);

	S evaluateDraw(P position);

}
