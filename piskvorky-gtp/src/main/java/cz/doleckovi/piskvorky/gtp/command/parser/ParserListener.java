package cz.doleckovi.piskvorky.gtp.command.parser;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.command.parser.GTPParserBaseListener;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
import cz.doleckovi.piskvorky.gtp.command.CommandFactoryImpl;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedList;
import java.util.Objects;

public class ParserListener extends GTPParserBaseListener {

    private final CommandFactory factory;

    ParserListener(CommandFactoryImpl factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    @Override
    public void exitWhiteColor(GTPParser.WhiteColorContext ctx) {
        ctx.value = Side.WHITE;
    }

    @Override
    public void exitBlackColor(GTPParser.BlackColorContext ctx) {
        ctx.value = Side.BLACK;
    }

    @Override
    public void exitNormalMove(GTPParser.NormalMoveContext ctx) {
        ctx.value = new Move(ctx.color().value.stone, getVertex(ctx.VERTEX()));
    }

    @Override
    public void exitBlockMove(GTPParser.BlockMoveContext ctx) {
        ctx.value = new Move(Stone.BLOCK, getVertex(ctx.VERTEX()));
    }

    @Override
    public void exitMoves(GTPParser.MovesContext ctx) {
        var moves = new LinkedList<Move>();
        for (var move : ctx.move()) {
            moves.add(move.value);
        }
        ctx.value = moves;
    }

    @Override
    public void exitSimpleCommand(GTPParser.SimpleCommandContext ctx) {
        ctx.value = factory.createSimpleCommand(ctx.COMMAND().getText());
    }

    @Override
    public void exitNumberCommand(GTPParser.NumberCommandContext ctx) {
        ctx.value = factory.createNumberCommand(ctx.COMMAND().getText(), ctx.INTEGER().getText());
    }

    @Override
    public void exitColorCommand(GTPParser.ColorCommandContext ctx) {
        ctx.value = factory.createColorCommand(ctx.COMMAND().getText(), ctx.color().value);
    }

    @Override
    public void exitVertexCommand(GTPParser.VertexCommandContext ctx) {
        ctx.value = factory.createVertexCommand(ctx.COMMAND().getText(), getVertex(ctx.VERTEX()));
    }

    @Override
    public void exitMoveCommand(GTPParser.MoveCommandContext ctx) {
        ctx.value = factory.createMoveCommand(ctx.COMMAND().getText(), ctx.move().value);
    }

    @Override
    public void exitMovesCommand(GTPParser.MovesCommandContext ctx) {
        ctx.value = factory.createMovesCommand(ctx.COMMAND().getText(), ctx.moves().value);
    }

    @Override
    public void exitKVPCommand(GTPParser.KVPCommandContext ctx) {
        ctx.value = factory.createKVPCommand(ctx.COMMAND().getText(), ctx.KEY().getText(), ctx.TEXT().getText());
    }

    @Override
    public void exitTextCommand(GTPParser.TextCommandContext ctx) {
        ctx.value = factory.createTextCommand(ctx.COMMAND().getText(), ctx.TEXT().getText());
    }

	@Override
	public void exitAction(GTPParser.ActionContext ctx) {
		if (ctx.INTEGER() == null) {
			ctx.value = ctx.command().value;
		} else {
			ctx.value = factory.decorateCommand(ctx.INTEGER().getText(), ctx.command().value);
		}
	}

	@Override
	public void exitInterruptCommand(GTPParser.InterruptCommandContext ctx) {
		ctx.value = factory.createInterruptCommand();
	}

	@Override
	public void exitEmptyCommand(GTPParser.EmptyCommandContext ctx) {
		ctx.value = factory.createEmptyCommand();
	}

	private Vertex getVertex(TerminalNode vertex) {
        return Vertex.parse(vertex.getText());
    }
}
