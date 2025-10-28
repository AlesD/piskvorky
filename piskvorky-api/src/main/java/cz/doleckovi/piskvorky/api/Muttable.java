package cz.doleckovi.piskvorky.api;

/** Interface implemented by mutable objects. */
public interface Muttable<SELF extends Muttable<SELF>> {

    SELF copy();

}
