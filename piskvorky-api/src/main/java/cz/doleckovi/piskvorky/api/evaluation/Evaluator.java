package cz.doleckovi.piskvorky.api.evaluation;

import cz.doleckovi.piskvorky.api.position.Position;

/** Position evaluator.
 * @param <P> Position type
 * @param <S> Score type
 */
@FunctionalInterface
public interface Evaluator<P extends Position<P>, S extends Score<S>> {

	/** Evaluate position.
	 * <p>Equal positions must produce same evaluation.</p>
	 * @param position Position to evaluate
	 * @return Position score
	 */
	S evaluate(P position);

}
