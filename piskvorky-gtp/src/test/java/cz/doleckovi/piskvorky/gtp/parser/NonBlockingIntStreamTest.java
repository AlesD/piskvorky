package cz.doleckovi.piskvorky.gtp.parser;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

class NonBlockingIntStreamTest implements WithAssertions {

	private AtomicInteger source;
	private NonBlockingIntStream stream;

	@BeforeEach
	void setup() {
		source = new AtomicInteger();
		stream = new NonBlockingIntStream(source::incrementAndGet);
	}

	@Test
	void testConsume() {
		var lookAhead = stream.LA(1);
		var index = stream.index();
		stream.consume();
		assertThat(stream.index()).isGreaterThan(index);
		assertThat(stream.LA(-1)).isEqualTo(lookAhead);
	}

	@Test
	void testSeek() {
		// LA(10) will force to read 10 integers, but index does not move
		stream.LA(10);
		assertThat(stream.index()).isEqualTo(0);
		assertThat(source.get()).isEqualTo(10);

		// Seek to 5 moves index, but does not read anything
		stream.seek(5);
		assertThat(stream.index()).isEqualTo(5);
		assertThat(source.get()).isEqualTo(10);

		// Seek to 15, moves index and forces to read up to 15 (indexes 0..14)
		stream.seek(15);
		assertThat(stream.index()).isEqualTo(15);
		assertThat(source.get()).isEqualTo(15);

		// LA(1) forces to read next integer
		assertThat(stream.LA(1)).isEqualTo(16);
		assertThat(source.get()).isEqualTo(16);

		// Initial buffer size is 256 - seek to 260 forces wrap, tail will be 260 % 256 = 4
		stream.seek(260);
		assertThatCode(() -> stream.seek(4)).doesNotThrowAnyException();
		assertThatThrownBy(() -> stream.seek(3)).isInstanceOf(UnsupportedOperationException.class);

		// Seek to negative index is not allowed
		assertThatThrownBy(() -> stream.seek(-1)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testMark() {
		stream.seek(260);
		var marker = stream.mark();
		stream.seek(1200);
		assertThat(source.get()).isEqualTo(1200);
		assertThatCode(() -> stream.seek(260)).doesNotThrowAnyException();
		stream.release(marker);
	}

}