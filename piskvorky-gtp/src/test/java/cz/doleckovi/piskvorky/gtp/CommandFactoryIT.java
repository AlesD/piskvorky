package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.gtp.command.*;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommandFactoryTest implements WithAssertions {

	private static final Move MOVE1 = new Move(Stone.WHITE, Vertex.parse("a1"));
	private static final Move MOVE2 = new Move(Stone.BLACK, Vertex.parse("b2"));

    @Autowired
    CommandFactory commandFactory;

    @Test
    void testCreateSimpleCommand() {
        assertThat(commandFactory.createSimpleCommand("list_commands"))
		        .isInstanceOf(ListCommandsCommand.class);
    }

	@Test
	void testCreateSideCommand() {
		assertThat(commandFactory.createSideCommand("reg_genmove", Side.WHITE))
				.isInstanceOf(RegGenMoveCommand.class);
	}

	@Test
	void testCreateTextCommand() {
		assertThat(commandFactory.createTextCommand("echo", "test"))
				.isInstanceOf(EchoCommand.class);
	}

	@Test
	void testCreateNumberCommand() {
		assertThat(commandFactory.createNumberCommand("boardsize", 10))
				.isInstanceOf(BoardSizeCommand.class);
	}

	@Test
	void testCreateVertexCommand() {
		assertThat(commandFactory.createVertexCommand("color", Vertex.parse("a1")))
				.isInstanceOf(ColorCommand.class);
	}

	@Test
	void testCreateMoveCommand() {
		assertThat(commandFactory.createMoveCommand("play", List.of(MOVE1)))
				.isInstanceOf(PlayCommand.class);
		assertThat(commandFactory.createMoveCommand("gogui-setup", List.of(MOVE1, MOVE2)))
				.isInstanceOf(GoGuiSetupCommand.class);
	}

	@Test
	void testCreateKVPCommand() {
		assertThat(commandFactory.createKVPCommand("piskvorky-config", "key", "value")).isInstanceOf(PiskvorkyConfigCommand.class);
	}

}
