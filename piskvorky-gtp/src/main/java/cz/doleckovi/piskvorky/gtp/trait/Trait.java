package cz.doleckovi.piskvorky.gtp.trait;

/** Represents intent that an object claims.
 * <p>This is just marker for trait interfaces. A trait declares no methods - behavior
 * belongs to the interface of the object that implements it, not to a trait copied onto
 * another object (compare {@link Cancelable}, whose implementation stays with its
 * declarer and is never copied).</p> */
public interface Trait {
    // Empty
}
