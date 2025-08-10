package cz.doleckovi.piskvorky.api.search;

import cz.doleckovi.piskvorky.api.board.Position;

/** Position evaluator.
 * @param <P> Position type
 * @param <S> Score type
 */
@FunctionalInterface
public interface Evaluator<P extends Position<P>, S extends Score<S>> {

	/** Evaluate position.
	 * <p>Equal positions must produce equal scores.</p>
	 * @param position Position to evaluate
	 * @return Score for given position
	 */
	S evaluate(P position);

}
