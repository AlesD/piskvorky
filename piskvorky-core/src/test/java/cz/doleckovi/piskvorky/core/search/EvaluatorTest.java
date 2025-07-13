package cz.doleckovi.piskvorky.core.search;
//
//import cz.doleckovi.piskvorky.api.board.Stone;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvFileSource;
//
//import java.util.Arrays;
//
//import static cz.doleckovi.piskvorky.core.evaluator.RowValueAssert.assertThat;
//
//public class EvaluatorTest {
//
//    void shortRow() {
//        var stones = new Stone[Pattern.LENGTH - 1];
//        Arrays.fill(stones, Stone.EMPTY);
//        var value = Evaluator.DEFAULT.evaluate(stones);
//	    assertThat(value).whites().containsOnly(Pattern.NONE);
//        assertThat(value).blacks().containsOnly(Pattern.NONE);
//    }
//
//    @Test
//    void longEnoughRow() {
//        var stones = new Stone[Pattern.LENGTH];
//        Arrays.fill(stones, Stone.EMPTY);
//        var value = Evaluator.DEFAULT.evaluate(stones);
//        assertThat(value).whites().containsOnly(Pattern.ONE);
//        assertThat(value).blacks().containsOnly(Pattern.ONE);
//    }
//
//    @ParameterizedTest
//    @CsvFileSource(resources = {"/patterns.csv"})
//    void evaluation(String stones, String whiteValues, String blackValues) {
//        assertThat(stones).hasWhiteValues(whiteValues).hasBlackValues(blackValues);
//    }
//
//}
