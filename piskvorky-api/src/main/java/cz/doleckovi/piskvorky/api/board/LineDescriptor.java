package cz.doleckovi.piskvorky.api.board;

import cz.doleckovi.piskvorky.api.Immutable;

/** Line descriptor. */
public interface LineDescriptor extends Immutable {

	/** Gets line direction.
	 * @return Line direction
	 */
	Direction direction();

	/** Gets line length.
	 * @return Line length
	 */
	int length();

	/** Gets field address.
	 * @param offset Offset from the line start
	 * @return Field address
	 */
	FieldAddress field(int offset);

}
