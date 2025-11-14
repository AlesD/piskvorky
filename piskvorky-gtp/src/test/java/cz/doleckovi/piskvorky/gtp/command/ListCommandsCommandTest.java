package cz.doleckovi.piskvorky.gtp.command;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class ListCommandsCommandTest implements WithAssertions {

	@Test
	void testIfKnowsItself() {
		assertThat(ListCommandsCommand.INSTANCE.execute(null))
				.contains(ListCommandsCommand.NAME);
	}
}