package cz.doleckovi.piskvorky.core.board;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static cz.doleckovi.piskvorky.api.Constants.WIN_LENGTH;

@DisplayName("Board tests")
class BoardImplTest implements WithAssertions {

    @Test
    @DisplayName("Does not allow board smaller than WIN_LENGTH")
    void doesNotAllowBoardSmallerThanWinLength() {
        assertThatThrownBy(() -> new BoardImpl(WIN_LENGTH - 1))
                .as("Board can't be smaller than %d", WIN_LENGTH)
                .isInstanceOf(IllegalArgumentException.class);
    }

}
