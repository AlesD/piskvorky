package cz.doleckovi.piskvorky.api.board;

/** Cell address.
 * <p>Helps to abstract internal organization of position.</p>
 */
public interface CellAddress {

	/** Gets cell offset.
	 * @return Cell offset
	 */
	int offset();

}
