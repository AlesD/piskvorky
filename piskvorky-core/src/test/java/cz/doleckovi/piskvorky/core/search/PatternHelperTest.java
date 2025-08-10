package cz.doleckovi.piskvorky.core.search;

import cz.doleckovi.piskvorky.api.board.Stone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PatternHelperTest {

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
		var defaultValues = PatternHelper.defaultPatterns();
		assertThat(defaultValues[PatternHelper.bits("XOOOOO")].classes)
				.hasSize(5)
				.containsOnly(StoneClass.FIVE)
				.isSameAs(defaultValues[PatternHelper.bits("-OOOOO")].classes);
		assertThat(defaultValues[PatternHelper.bits("X-OO--")].classes)
				.containsExactly(StoneClass.THREE, StoneClass.TWO, StoneClass.TWO, StoneClass.THREE, StoneClass.THREE)
				.isSameAs(defaultValues[PatternHelper.bits("--OO--")].classes);
		for (int index = 0; index < defaultValues.length; ++index)
			assertThat(defaultValues[index].bits).isEqualTo(index);
	}

	@Test
	void toString_() {
		assertThat(PatternHelper.toString(0b110000))
				.isEqualTo(new StringBuilder().append(Stone.BLACK).append(Stone.WHITE).repeat(Stone.EMPTY.toString(), 4).toString());
		assertThat(PatternHelper.toString(0b000001))
				.isEqualTo(new StringBuilder().repeat(Stone.EMPTY.toString(),5).append(Stone.WHITE).toString());
	}

}
