package cz.doleckovi.piskvorky.api.board;

/** Board on which games are played.
 * <p>Board instances must be thread safe and immutable.</p>
 */
public interface Board<P extends Position<P>> {

	/** Gets board width.
	 * @return Board width
	 */
	int width();

	/** Gets board height.
	 * @return Board height
	 */
	int height();

	/** Initial <strong>empty</strong> position.
	 * @return Initial position
	 */
	P initialPosition();

}
