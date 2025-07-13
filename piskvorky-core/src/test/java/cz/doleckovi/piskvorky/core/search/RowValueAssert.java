package cz.doleckovi.piskvorky.core.search;
//
//import cz.doleckovi.piskvorky.api.board.Stone;
//import org.assertj.core.api.AbstractObjectAssert;
//import org.assertj.core.api.ObjectArrayAssert;
//
//import static org.assertj.core.api.InstanceOfAssertFactories.array;
//
//public class RowValueAssert extends AbstractObjectAssert<RowValueAssert, RowValue> {
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
//    public static RowValueAssert assertThat(RowValue value) {
//        return new RowValueAssert(value);
//    }
//
//    public static RowValueAssert assertThat(String stones) {
//        return new RowValueAssert(Evaluator.DEFAULT.evaluate(stones(stones)));
//    }
//
//    protected RowValueAssert(RowValue value) {
//        super(value, RowValueAssert.class);
//    }
//
//    public ObjectArrayAssert<Pattern> whites() {
//        return extracting(RowValue::whites, array(Pattern[].class));
//    }
//
//    public ObjectArrayAssert<Pattern> blacks() {
//        return extracting(RowValue::blacks, array(Pattern[].class));
//    }
//
//    public RowValueAssert hasWhiteValues(Pattern... values) {
//        isNotNull();
//        whites().containsExactly(values);
//        return this;
//    }
//
//    public RowValueAssert hasWhiteValues(String values) {
//        return hasWhiteValues(values(values));
//    }
//
//    public RowValueAssert hasBlackValues(Pattern... values) {
//        isNotNull();
//        blacks().containsExactly(values);
//        return this;
//    }
//
//    public RowValueAssert hasBlackValues(String values) {
//        return hasBlackValues(values(values));
//    }
//
//}
