package cz.doleckovi.piskvorky.api.board;

import java.util.ListIterator;

/** Iterator for navigation over field indexes of board. */
public interface FieldIterator<T extends Field> extends ListIterator<T> {

	/** Iterator in reverse direction.
	 * @return Iterator with same <em>previous</em> index but advancing in opposite direction
	 */
	FieldIterator<T> reverseIterator();

}
