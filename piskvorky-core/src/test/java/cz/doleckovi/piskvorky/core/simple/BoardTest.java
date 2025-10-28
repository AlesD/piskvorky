package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Board;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.ValueSource;

@ParameterizedClass
@ValueSource(classes = {SimpleBoard.class})
class BoardTest implements WithAssertions {

    @Parameter
    Class<Board> boardClass;

    @Nested
    @ParameterizedClass
    @ValueSource(ints = {5, 10, 15})
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class SizeDependentTest {

        @Parameter
        int size;

        Board board;

        @BeforeAll
        void beforeAll() throws Exception {
            board = boardClass.getDeclaredConstructor(Integer.TYPE).newInstance(size);
        }

        @Test
        void testFieldOwnership() {
            int index = board.fieldCount();
            while (--index >= 0) {
                var field = board.field(index);
                assertThat(field).as("Field %d is not null", index).isNotNull();
                assertThat(field.index()).as("Field index %d matches index used to retrieve it", index).isEqualTo(index);
                assertThat(board.field(index)).as("Board returns identical fields", index).isSameAs(field);
            }
        }

    }

}
