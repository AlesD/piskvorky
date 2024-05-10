package cz.doleckovi.piskvorky.api.traits;

/** Value trait.
 * <p>Implemented by object that have notion of <em>value</em>. Value allows comparing objects and determine which
 * objects are <em>better</em> or more important. Value semantic is determined by the class implementing this trait.</p>
 */
public interface ValueTrait {

	/** Gets object value.
	 * @return Object value
	 */
	int value();

}
