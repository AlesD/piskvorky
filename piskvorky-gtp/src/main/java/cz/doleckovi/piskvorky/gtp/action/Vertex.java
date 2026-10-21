package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.api.board.FieldAddress;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Arrays;

/** Representation of vertex entity (spec &sect;3.2.1).
 * <p>Special value {@code pass} is represented by {@code null}.</p>
 * @param column Board column (zero based)
 * @param row Board row (zero based)
 */
public record Vertex(int column, int row) {

    public static final String PASS = "pass";
    private static final char[] COLUMNS = "ABCDEFGHJKLMNOPQRSTUVWXYZ".toCharArray();

    /** Parse board coordinates into vertex.
     * @param coordinates Board coordinates (spec &sect;2.11) or {@code pass}
     * @return Parsed instance, {@code null} for "pass"
     */
    public static Vertex parse(String coordinates) {
        if (PASS.equalsIgnoreCase(coordinates))
            return null;
        try {
            var column = Arrays.binarySearch(COLUMNS, Character.toUpperCase(coordinates.charAt(0)));
            var row = Integer.parseInt(coordinates.substring(1)) - 1;
            return new Vertex(column, row);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid vertex: %s".formatted(coordinates));
        }
    }

    /** Converts field address to vertex.
     * @param field Field address
     * @return Vertex instance
     */
    public static Vertex of(FieldAddress field) {
        return new Vertex(field.column(), field.row());
    }

    public Vertex {
        if (column < 0 || column >= COLUMNS.length)
            throw new IllegalArgumentException("Invalid vertex column: %d".formatted(column));
        if (row < 0 || row >= COLUMNS.length)
            throw new IllegalArgumentException("Invalid vertex row: %d".formatted(row));
    }

    @Override
    public String toString() {
        return String.valueOf(COLUMNS[column]) + (row + 1);
    }

}
