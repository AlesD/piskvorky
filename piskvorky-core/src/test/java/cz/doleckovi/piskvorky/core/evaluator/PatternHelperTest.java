package cz.doleckovi.piskvorky.core.evaluator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PatternHelperTest {

	@Test
	void bitCount() {
		assertThat(PatternHelper.bitCount(0b000)).isEqualTo(0);
		assertThat(PatternHelper.bitCount(0b001)).isEqualTo(1);
		assertThat(PatternHelper.bitCount(0b010)).isEqualTo(1);
		assertThat(PatternHelper.bitCount(0b100)).isEqualTo(1);
		assertThat(PatternHelper.bitCount(0b011)).isEqualTo(2);
		assertThat(PatternHelper.bitCount(0b101)).isEqualTo(2);
		assertThat(PatternHelper.bitCount(0b110)).isEqualTo(2);
		assertThat(PatternHelper.bitCount(0b111)).isEqualTo(3);
	}

	@Test
	void bits() {
		assertThatThrownBy(() -> PatternHelper.bits("-----"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageStartingWith("Pattern must contain exactly");
		assertThatThrownBy(() -> PatternHelper.bits("-------"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageStartingWith("Pattern must contain exactly");
		assertThat(PatternHelper.bits("------")).isEqualTo(0b000000);
		assertThat(PatternHelper.bits("X-O-O-")).isEqualTo(0b101010);
		assertThatThrownBy(() -> PatternHelper.bits("O-----"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("White stone at pattern start");
		assertThatThrownBy(() -> PatternHelper.bits("-X----"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Black stone inside pattern");
	}

	@Test
	void defaultValues() {
		var defaultValues = PatternHelper.defaultValues();
		assertThat(defaultValues[PatternHelper.bits("XOOOOO")])
				.hasSize(5)
				.containsOnly(Pattern.FIVE_IN_ROW)
				.isSameAs(defaultValues[PatternHelper.bits("-OOOOO")]);
		assertThat(defaultValues[PatternHelper.bits("X-OO--")])
				.containsExactly(Pattern.THREE, Pattern.TWO, Pattern.TWO, Pattern.THREE, Pattern.THREE)
				.isSameAs(defaultValues[PatternHelper.bits("--OO--")]);
	}

	@Test
	void toString_() {
		assertThat(PatternHelper.toString(0b110000)).isEqualTo("⚫⚪····");
		assertThat(PatternHelper.toString(0b100001)).isEqualTo("⚫····⚪");
	}

}
