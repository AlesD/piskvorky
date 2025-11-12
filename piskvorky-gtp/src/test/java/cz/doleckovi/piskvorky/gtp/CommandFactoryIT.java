package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.gtp.command.EchoCommand;
import cz.doleckovi.piskvorky.gtp.command.ListCommandsCommand;
import cz.doleckovi.piskvorky.gtp.command.ProtocolVersionCommand;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommandFactoryTest implements WithAssertions {

    @Autowired
    CommandFactory commandFactory;

    @Test
    void testListCommandsCommand() {
        var command = commandFactory.createSimpleCommand(ListCommandsCommand.NAME);
        assertThat(command.execute(null).split("\n"))
                .contains(ListCommandsCommand.NAME)    // Itself
                .contains(ProtocolVersionCommand.NAME) // Singleton bean
                .contains(EchoCommand.NAME)            // Prototype bean
        ;
    }

    @Test
    void testTextCommandCreation() {
        var command = commandFactory.createTextCommand(EchoCommand.NAME, "test");
        assertThat(command.execute(null)).isEqualTo("test");
    }
}
