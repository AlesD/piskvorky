package cz.doleckovi.piskvorky.api.evaluation;

import cz.doleckovi.piskvorky.api.board.CellAddress;
import cz.doleckovi.piskvorky.api.position.*;

/** Position evaluator. */
@FunctionalInterface
public interface LineUpdateHandler<X extends EvaluationContext, L extends LineData, C extends CellData> {

	L updateLine(X context, int offset, Stone stone, L lineData, C[] cellsData);

}
