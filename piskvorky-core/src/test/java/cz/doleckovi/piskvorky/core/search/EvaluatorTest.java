package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.core.board.BoardTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Arrays;

import static cz.doleckovi.piskvorky.core.search.RowValueAssert.assertThat;

public class EvaluatorTest {

    @ParameterizedTest
    @CsvFileSource(resources = {"/patterns.csv"})
    void evaluation(String stones, String whiteValues, String blackValues) {
        assertThat(stones)
                .hasWhiteValues(whiteValues)
                .hasBlackValues(blackValues);
    }

}
