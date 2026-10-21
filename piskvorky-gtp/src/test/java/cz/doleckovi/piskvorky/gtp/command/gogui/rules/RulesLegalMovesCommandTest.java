package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.TestContext;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The GoGui rules report must be <em>complete</em>: every empty field listed (in field
 * index order), occupied fields absent — independent of any engine candidate filtering.
 * Commands read the board through their execution {@link cz.doleckovi.piskvorky.gtp.action.Context}.
 */
class RulesLegalMovesCommandTest {

	@Test
	void emptyBoardListsEveryField() throws Exception {
		var answer = new RulesLegalMovesCommand().execute(new TestContext());
		assertThat(answer.split(" ")).hasSize(19 * 19);
	}

	@Test
	void occupiedFieldIsNotALegalMove() throws Exception {
		var context = new TestContext();
		context.getGame().move(Side.BLACK, 0, 0);
		var answer = new RulesLegalMovesCommand().execute(context);
		assertThat(answer.split(" ")).hasSize(19 * 19 - 1);
		assertThat(answer.split(" ")).doesNotContain(new Vertex(0, 0).toString());
	}

	@Test
	void terminalPositionListsNoMoves() throws Exception {
		var context = new TestContext();
		// horizontal five in a row for Black: alternating moves, row 0 vs row 1
		for (int column = 0; column < 5; column++) {
			context.getGame().move(Side.BLACK, column, 0);
			if (column < 4)
				context.getGame().move(Side.WHITE, column, 1);
		}
		assertThat(new RulesLegalMovesCommand().execute(context)).isEmpty();
	}

}
