package cz.doleckovi.piskvorky.api.ui;

import cz.doleckovi.piskvorky.api.traits.IdTrait;

import java.util.List;

/** Row of the board for specific position. */
public interface Row extends IdTrait<Integer> {
	/** Fields on the line. */
	List<? extends Field> getFields();
}
