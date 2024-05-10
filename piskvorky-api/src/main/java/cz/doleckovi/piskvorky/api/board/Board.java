package cz.doleckovi.piskvorky.api.board;

/** Board on which games are played.
 * <p>Board instances must be immutable and can be shared by positions from many games.</p>
 */
public interface Board {

	/** Gets board width.
	 * <p>Board width is always greater or equal to 5.</p>
	 * @return Board width
	 */
	int getWidth();

	/** Gets board height.
	 * <p>Board height is always greater or equal to 5.</p>
	 * @return Board height
	 */
	int getHeight();

	/** Initial <strong>empty</strong> position.
	 * @return Initial position
	 */
	Position initialPosition();

}
