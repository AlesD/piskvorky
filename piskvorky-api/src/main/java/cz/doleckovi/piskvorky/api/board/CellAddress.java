package cz.doleckovi.piskvorky.api.board;

/** Cell address.
 * <p>Helps to abstract internal organization of position.</p>
 * @param line Line index
 * @param offset Cell offset
 */
public record CellAddress(int line, int offset) { }
