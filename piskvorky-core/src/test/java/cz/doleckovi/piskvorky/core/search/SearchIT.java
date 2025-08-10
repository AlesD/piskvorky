package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.api.search.Move;
import cz.doleckovi.piskvorky.api.search.Player;
import cz.doleckovi.piskvorky.core.board.BoardImpl;
import cz.doleckovi.piskvorky.core.board.MoveImpl;
import cz.doleckovi.piskvorky.core.board.PositionImpl;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.regex.Pattern;

class SearchIT implements WithAssertions {

    private static final Pattern COMMENT = Pattern.compile("^#.*$");
    private static final Pattern IGNORED = Pattern.compile("[0-9 <>]+");
    private static final Pattern SOLUTION = Pattern.compile("(?<depth>[0-9]+): *\\[(?<column>[0-9]+), *(?<row>[0-9]+)](?<terminal>!?)");

    private static String readLine(BufferedReader reader) throws IOException {
        var line = reader.readLine();
        while (line != null) {
            line = COMMENT.matcher(line).replaceAll("");
            if (!line.isBlank())
                break;
            line = reader.readLine();
        }
        return line;
    }

    static Stone[] stones(BufferedReader reader) throws IOException {
        Stone[] result = null;
        while (result == null) {
            var line = readLine(reader);
            if (line == null)
                return null;
            line = IGNORED.matcher(line).replaceAll("");
            var size = line.length();
            if (size == 0)
                continue;
            result = new Stone[size];
            for (var index = 0; index < size; ++index)
                result[index] = Stone.valueOf(line.charAt(index));
        }
        return result;
    }

    static Stream<Arguments> search() throws IOException {
        var result = new LinkedList<Arguments>();
        var is = SearchIT.class.getResourceAsStream("/positions.txt");
        Assertions.assertThat(is).isNotNull();
        try (var reader = new BufferedReader(new InputStreamReader(is))) {
            while (true) {
                var stones = stones(reader);
                if (stones == null)
                    break;
                var size = stones.length;
                var position = new BoardImpl(size).initialPosition();
                for (var row = 0; row < size; ++row) {
                    if (row != 0) {
                        stones = stones(reader);
                        Assertions.assertThat(stones)
                                .isNotNull()
                                .hasSize(size);
                    }
                    for (var column = 0; column < size; ++column)
                        position = position.withStone(column, row, stones[column]);
                }
                var line = readLine(reader);
                var solution = line == null ? null : SOLUTION.matcher(line);
                Assertions.assertThat(solution)
                        .as("Line with solution follows position").isNotNull()
                        .as("Line %s matches %s", line, SOLUTION).matches();
                var depth = Integer.parseInt(solution.group("depth"));
                var column = Integer.parseInt(solution.group("column"));
                var row = Integer.parseInt(solution.group("row"));
                var terminal = solution.group("terminal").equals("!");
                var move = new MoveImpl(Player.WHITE, column, row);
                result.add(Arguments.argumentSet("Position %d".formatted(result.size() + 1),
                        position, depth, move, terminal));
            }
        }
        return result.stream();
    }

    @ParameterizedTest
    @MethodSource
    void search(PositionImpl position, int depth, Move move, boolean terminal) throws InterruptedException {
        assertThat(new Minimax<>(new MoveGeneratorImpl<>(), EvaluatorImpl.DEFAULT).search(position, Player.WHITE, depth))
                .isNotEmpty()
                .contains(move);
    }

}
