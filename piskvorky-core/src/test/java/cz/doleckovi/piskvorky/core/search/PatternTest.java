package cz.doleckovi.piskvorky.core.search;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternTest {

    @Test
    void bitCount() {
        assertThat(Pattern.bitCount(0b000)).isEqualTo(0);
        assertThat(Pattern.bitCount(0b001)).isEqualTo(1);
        assertThat(Pattern.bitCount(0b010)).isEqualTo(1);
        assertThat(Pattern.bitCount(0b100)).isEqualTo(1);
        assertThat(Pattern.bitCount(0b011)).isEqualTo(2);
        assertThat(Pattern.bitCount(0b101)).isEqualTo(2);
        assertThat(Pattern.bitCount(0b110)).isEqualTo(2);
        assertThat(Pattern.bitCount(0b111)).isEqualTo(3);
        assertThat(Pattern.bitCount(Pattern.PLAYER_MASK)).isEqualTo(Pattern.LENGTH);
        assertThat(Pattern.bitCount(Pattern.OPPONENT_MASK)).isEqualTo(0);
        assertThat(Pattern.bitCount(Integer.MIN_VALUE)).isEqualTo(0);
    }

}
