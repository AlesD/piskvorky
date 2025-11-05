package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ValueSource;

@ParameterizedClass
@ValueSource(ints = {5, 10, 15})
@DisplayName("Tests for different sizes of board")
class VariableSizeBoardTest implements WithAssertions {

    @Parameter
    int size;

    private BoardImpl board;

    @BeforeEach
    void createBoard() {
        board = new BoardImpl(size);
    }

    @Test
    @DisplayName("Field => Cell => Line => Field")
    void checkField() {
        for (int row = 0; row < board.size(); ++row)
            for (int column = 0; column < board.size(); ++column) {
                var field = board.field(column, row);
                for (var direction : Direction.values()) {
                    var cell = field.cell(direction);
                    var line = board.line(cell.line());
                    assertThat(line.field(cell.offset()))
                            .as("Holds for %s and %s", field, direction)
                            .isSameAs(field);
                }
            }
    }
}
