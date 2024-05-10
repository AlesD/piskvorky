package cz.doleckovi.piskvorky.core.board;

//import cz.doleckovi.piskvorky.api.board.Stone;
//import cz.doleckovi.piskvorky.core.evaluator.*;
//import org.assertj.core.api.Assertions;
//import org.assertj.core.api.InstanceOfAssertFactory;
//import org.assertj.core.api.ObjectArrayAssert;
//
//import static org.assertj.core.api.InstanceOfAssertFactories.array;
//
//public class RowAssert extends GenerationAssert<RowAssert, Line> {
//
//    private static Stone[] stones(String stones) {
//        var result = new Stone[stones.length()];
//        for (int fieldIndex = 0; fieldIndex < result.length; ++fieldIndex)
//            result[fieldIndex] = Stone.valueOf(stones.charAt(fieldIndex));
//        return result;
//    }
//
//    private static Pattern[] values(String values) {
//        var result = new Pattern[values.length()];
//        for (int fieldIndex = 0; fieldIndex < result.length; ++fieldIndex)
//            result[fieldIndex] = switch (values.charAt(fieldIndex)) {
//                case '0' -> Pattern.NONE;
//                case '1' -> Pattern.ONE;
//                case '2' -> Pattern.TWO;
//                case '3' -> Pattern.THREE;
//                case '4' -> Pattern.FOUR;
//                case '5' -> Pattern.FIVE_IN_ROW;
//                default -> null;
//            };
//        return result;
//    }
//
//    public static RowAssert assertThat(Line line) {
//        return new RowAssert(line);
//    }
//
//    public static RowAssert assertThat(String stones) {
//        return new RowAssert(new Line(0, stones(stones), Evaluator.DEFAULT));
//    }
//
//    protected RowAssert(final Line line) {
//        super(line, RowAssert.class);
//    }
//
//    public ObjectArrayAssert<Stone> stones() {
//        return extracting("stones", array(Stone[].class));
//    }
//
//    public RowValueAssert values() {
//        return extracting("value", new InstanceOfAssertFactory<>(RowValue.class, RowValueAssert::assertThat));
//    }
//
//    public RowAssert hasStones(Stone... stones) {
//        isNotNull();
//        stones().containsExactly(stones);
//        return this;
//    }
//
//    public RowAssert hasStones(String stones) {
//        return hasStones(stones(stones));
//    }
//
//}
