package cz.doleckovi.piskvorky.gtp;

import java.util.regex.Pattern;

public class Vertex {

    private static final Pattern PATTERN = Pattern.compile("(?<column>[a-hA-H][j-zJ-Z])(?<row>[1-9][0-9]?)");
    private final char column;
    private final int row;

    private Vertex(char column, int row) {
        this.column = column;
        this.row = row;
    }

    Vertex parse(String vertex) {
        var matcher = PATTERN.matcher(vertex);
        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid vertex: %s".formatted(vertex));
        return new Vertex(matcher.group("column").charAt(0), Integer.parseInt(matcher.group("row")));
    }

    @Override
    public String toString() {
        return String.valueOf(column) + row;
    }

}
