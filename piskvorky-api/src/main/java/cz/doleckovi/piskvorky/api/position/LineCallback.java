package cz.doleckovi.piskvorky.api.position;

/** Callbacks for changes in line and cell data.
 * @param <L> Line data type
 * @param <C> Cell data type
 */
public interface LineCallback<L extends LineData, C extends CellData> {

	/** Callback for change in cell data.
	 * @param offset Cell offset
	 * @param oldData Old data
	 * @param newData New data
	 */
	default void cellDataChanged(int offset, C oldData, C newData) {
		// do nothing
	}

	/** Callback for change in line data.
	 * @param oldData Old data
	 * @param newData New data
	 */
	default void lineDataChanged(L oldData, L newData) {
		// do nothing
	}

}
