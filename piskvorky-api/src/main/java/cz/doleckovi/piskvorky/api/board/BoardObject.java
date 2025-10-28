package cz.doleckovi.piskvorky.api.board;

/** Board if fully defined by its size.
 * <p>All objects with same board size belong to same board. */
public interface BoardObject {

	/** Gets minimum board size.
	 * @return Minimum board size
	 */
	static int minSize() {
		return 5;
	}

	/** Gets board size.
	 * @return Board size
	 */
	int size();

}
