package cz.doleckovi.piskvorky.api;

/** Helper interface allowing generic types to return <code>this</code> in type safe manner.
 * @param <SELF> Generic type object
 */
public interface Self<SELF extends Self<SELF>> {

	/** Return <code>this</code> typed as SELF.
	 * @return This instance cast to SELF
	 */
	@SuppressWarnings("unchecked")
	default SELF self() {
		return (SELF) this;
	}

}
