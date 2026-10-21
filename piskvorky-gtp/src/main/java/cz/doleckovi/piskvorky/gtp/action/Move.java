package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.api.position.Stone;

import java.util.StringJoiner;

/** Representation of move entity (spec &sect;3.2.1).
 * @param stone Stone instance
 * @param vertex Vertex instance or {@code null} for {@code pass}
 */
public record Move(Stone stone, Vertex vertex) {

	@Override
	public String toString() {
		return stone.toString() + " " + vertex == null ? "pass" : vertex.toString();
	}

}
