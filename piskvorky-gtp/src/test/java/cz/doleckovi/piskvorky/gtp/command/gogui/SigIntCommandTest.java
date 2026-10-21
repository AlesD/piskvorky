package cz.doleckovi.piskvorky.gtp.command.gogui;

import cz.doleckovi.piskvorky.gtp.action.TestContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SigIntCommandTest {

	@Test
	void answerIsPidOfThisProcess() throws Exception {
		var answer = new SigIntCommand().execute(TestContext.SILENT);
		assertThat(answer).isEqualTo(Long.toString(ProcessHandle.current().pid()));
	}

	@Test
	void factoryCreatesSigIntCommandForItsName() {
		var command = new GoGuiCommandFactory().createSimpleCommand(SigIntCommand.NAME);
		assertThat(command).isInstanceOf(SigIntCommand.class);
	}

	@Test
	void factoryAdvertisesSigIntCommand() {
		assertThat(new GoGuiCommandFactory().supportedCommands()).contains(SigIntCommand.NAME);
	}

}
