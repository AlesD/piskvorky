package cz.doleckovi.piskvorky.gtp.transport;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicBoolean;

class StoppableCommandReaderTest implements WithAssertions {

	@Test
	void stripsAllKindsOfTerminators() throws IOException {
		var reader = new StoppableCommandReader(new StringReader("a\nb\rc\r\nd"), () -> false);
		assertThat(reader.readLine()).isEqualTo("a");
		assertThat(reader.readLine()).isEqualTo("b");
		assertThat(reader.readLine()).isEqualTo("c");
		assertThat(reader.readLine()).isEqualTo("d");
		assertThat(reader.readLine()).isNull();
	}

	@Test
	void emptyLineIsEmptyLineNotEof() throws IOException {
		var reader = new StoppableCommandReader(new StringReader("\n\nx"), () -> false);
		assertThat(reader.readLine()).isEmpty();
		assertThat(reader.readLine()).isEmpty();
		assertThat(reader.readLine()).isEqualTo("x");
		assertThat(reader.readLine()).isNull();
	}

	@Test
	void deliveredAsSuppliedWhenInputEndsMidLine() throws IOException {
		var reader = new StoppableCommandReader(new StringReader("genmove"), () -> false);
		assertThat(reader.readLine()).as("trailing fragment surfaces on real EOF")
				.isEqualTo("genmove");
	}

	@Test
	void suppliedBufferIsUsedWithoutASecondBufferingLayer() throws IOException {
		var buffered = new CountingBufferedReader(new StringReader("genmove b\nquit\n"));
		var reader = new StoppableCommandReader(buffered, () -> false);
		assertThat(reader.readLine()).isEqualTo("genmove b");
		assertThat(reader.readLine()).isEqualTo("quit");
		assertThat(buffered.lineReads()).as("no BufferedReader stacked on the supplied one")
				.isEqualTo(2);
	}

	@Test
	void stopEndsInputAndDiscardsBufferedLines() throws IOException {
		var stopped = new AtomicBoolean(false);
		var reader = new StoppableCommandReader(new StringReader("genmove b\nquit\n"), stopped::get);
		assertThat(reader.readLine()).isEqualTo("genmove b");
		stopped.set(true);
		assertThat(reader.readLine()).as("input arrived before the stop is dropped")
				.isEqualTo(null);
	}

}
