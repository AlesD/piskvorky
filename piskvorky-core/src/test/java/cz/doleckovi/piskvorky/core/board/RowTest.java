package cz.doleckovi.piskvorky.core.board;

//import cz.doleckovi.piskvorky.api.board.Stone;
//import cz.doleckovi.piskvorky.core.evaluator.Evaluator;
//import cz.doleckovi.piskvorky.core.evaluator.EvaluatorTest;
//import cz.doleckovi.piskvorky.core.evaluator.Pattern;
//import org.junit.jupiter.api.Test;
//
//import static cz.doleckovi.piskvorky.core.board.RowAssert.assertThat;
//
//public class LineTest {
//
//    @Test
//    void newRowIsEmpty() {
//	    assertThat(new Line(Pattern.LENGTH, Evaluator.DEFAULT)).stones().containsOnly(Stone.EMPTY);
//    }
//
//    @Test
//    void setStone() {
//        var row1 = new Line(5, Evaluator.DEFAULT);
//
//        var row2 = row1.setStone(1, 2, Stone.WHITE);
//        assertThat(row2)
//                .isNotSameAs(row1)
//                .hasStones("--O--");
//
//        var undo = row2.setStone(2, 2, Stone.EMPTY);
//        assertThat(undo)
//                .isNotSameAs(row1)
//                .hasStones("-----");
//
//        var row3 = row2.setStone(2, 3, Stone.BLACK);
//        assertThat(row3)
//                .isNotSameAs(row2)
//                .hasStones("--OX-");
//    }
//
//}
