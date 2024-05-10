package cz.doleckovi.piskvorky.api.traits;

/** Generation trait.
 * <p>Used to track position changes.</p>
 */
public interface GenerationTrait {
	/** Generation from which this state originates.
	 * @return Move number that changed this object
	 */
	int generation();
}
