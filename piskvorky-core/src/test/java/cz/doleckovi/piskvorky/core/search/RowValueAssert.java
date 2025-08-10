package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.core.board.BoardTestUtils;
import cz.doleckovi.piskvorky.core.board.Line;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.ObjectArrayAssert;

import static org.assertj.core.api.InstanceOfAssertFactories.array;
import cz.doleckovi.piskvorky.core.search.EvaluatorImpl.LineEvaluation;

import java.util.Arrays;

public class RowValueAssert extends AbstractObjectAssert<RowValueAssert, LineEvaluation> {

    private static StoneClass[] values(String values) {
        var result = new StoneClass[values.length()];
        for (int fieldIndex = 0; fieldIndex < result.length; ++fieldIndex)
            result[fieldIndex] = switch (values.charAt(fieldIndex)) {
                case '0' -> StoneClass.NONE;
                case '1' -> StoneClass.ONE;
                case '2' -> StoneClass.TWO;
                case '3' -> StoneClass.THREE;
                case '4' -> StoneClass.FOUR;
                case '5' -> StoneClass.FIVE;
                default -> throw new IllegalArgumentException("Unsupported stone class %s".formatted(values.charAt(fieldIndex)));
            };
        return result;
    }

    static RowValueAssert assertThat(LineEvaluation value) {
        return new RowValueAssert(value);
    }

    public static RowValueAssert assertThat(String stones) {
        return new RowValueAssert(EvaluatorImpl.DEFAULT.evaluate(BoardTestUtils.line(stones)));
    }

    protected RowValueAssert(LineEvaluation value) {
        super(value, RowValueAssert.class);
    }

    public ObjectArrayAssert<StoneClass> whites() {
        return extracting(LineEvaluation::whites, array(StoneClass[].class));
    }

    public ObjectArrayAssert<StoneClass> blacks() {
        return extracting(LineEvaluation::blacks, array(StoneClass[].class));
    }

    public RowValueAssert hasWhiteValues(StoneClass... values) {
        isNotNull();
        whites().as("White values are %s".formatted(Arrays.toString(values))).containsExactly(values);
        return this;
    }

    public RowValueAssert hasWhiteValues(String values) {
        return hasWhiteValues(values(values));
    }

    public RowValueAssert hasBlackValues(StoneClass... values) {
        isNotNull();
        blacks().as("Black values are %s".formatted(Arrays.toString(values))).containsExactly(values);
        return this;
    }

    public RowValueAssert hasBlackValues(String values) {
        return hasBlackValues(values(values));
    }

}
