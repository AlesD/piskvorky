package cz.doleckovi.piskvorky.api.traits;

/** ID trait.
 * <p>Implemented by object that provide unique identification.</p>
 * @param <T> ID type
 */
public interface IdTrait<T> {

	/** Get object ID.
	 * @return Object ID
	 */
	T getId();

}
