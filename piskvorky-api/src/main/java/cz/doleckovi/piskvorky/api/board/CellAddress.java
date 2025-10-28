package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.Immutable;

/** Cell address.
 * <p>Helps to abstract internal organization of position.</p>
 */
public interface CellAddress extends Immutable {

	/** Gets line index.
	 * @return Line index
	 */
	int line();

	/** Gets cell offset.
	 * @return Cell offset
	 */
	int offset();

}
