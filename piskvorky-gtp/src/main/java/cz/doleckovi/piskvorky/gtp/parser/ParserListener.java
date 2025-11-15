package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ParserListener extends GTPParserBaseListener {

	private static final Logger LOG = LoggerFactory.getLogger(ParserListener.class);

    private final CommandFactory factory;
	private final Deque<Object> stack;

    ParserListener(CommandFactory factory, Deque<Object> stack) {
        this.factory = Objects.requireNonNull(factory);
		this.stack = stack;
    }

	private void push(Object object) {
		stack.push(object);
		LOG.trace("Pushed {}: {}", stack.size(), object);
	}

	private <T> T pop(Class<T> type) {
		LOG.trace("Pop {}: ({}) {}", stack.size(), type, stack.peek());
		var object = stack.pop();
		return type.cast(object);
	}

	@Override
	public void exitCommandWithId(GTPParser.CommandWithIdContext ctx) {
		if (ctx.id != null) {
			LOG.trace("Adding ID to command: {}", ctx.getText());
			var command = pop(Command.class);
			var id = ctx.id.getText();
			push(IdTrait.add(command, id));
		}
	}

	@Override
	public void exitInterruptAction(GTPParser.InterruptActionContext ctx) {
		LOG.trace("Creating interrupt action: {}", ctx.getText());
		push(factory.createInterruptAction());
	}

	@Override
	public void exitNoOpAction(GTPParser.NoOpActionContext ctx) {
		LOG.trace("Creating no-op action: {}", ctx.getText());
		push(factory.createNoOpAction());
	}

	@Override
	public void exitTextCommand(GTPParser.TextCommandContext ctx) {
		LOG.trace("Creating text command for: {}", ctx.getText());
		push(factory.createTextCommand(ctx.COMMAND().getText(), ctx.TEXT().getText()));
	}

	@Override
	public void exitKVPCommand(GTPParser.KVPCommandContext ctx) {
		LOG.trace("Creating key-value pair command for: {}", ctx.getText());
		push(factory.createKVPCommand(ctx.COMMAND().getText(), ctx.KEY().getText(), ctx.TEXT().getText()));
	}

	@Override
	public void exitMovesCommand(GTPParser.MovesCommandContext ctx) {
		LOG.trace("Creating moves command for: {}", ctx.getText());
		@SuppressWarnings("unchecked")
		List<Move> moves = pop(List.class);
		push(factory.createMovesCommand(ctx.COMMAND().getText(), moves));
	}

	@Override
	public void exitMoveCommand(GTPParser.MoveCommandContext ctx) {
		LOG.trace("Creating move command for: {}", ctx.getText());
		var move = pop(Move.class);
		push(factory.createMoveCommand(ctx.COMMAND().getText(), move));
	}

	@Override
	public void exitVertexCommand(GTPParser.VertexCommandContext ctx) {
		LOG.trace("Creating vertex command for: {}", ctx.getText());
		push(factory.createVertexCommand(ctx.COMMAND().getText(), Vertex.parse(ctx.VERTEX().getText())));
	}

	@Override
	public void exitSideCommand(GTPParser.SideCommandContext ctx) {
		LOG.trace("Creating side command for: {}", ctx.getText());
		var side = pop(Side.class);
		push(factory.createSideCommand(ctx.COMMAND().getText(), side));
	}

	@Override
	public void exitNumberCommand(GTPParser.NumberCommandContext ctx) {
		LOG.trace("Creating number command for: {}", ctx.getText());
		push(factory.createNumberCommand(ctx.COMMAND().getText(), ctx.INTEGER().getText()));
	}

	@Override
	public void exitSimpleCommand(GTPParser.SimpleCommandContext ctx) {
		LOG.trace("Creating simple command for: {}", ctx.getText());
		push(factory.createSimpleCommand(ctx.COMMAND().getText()));
	}

	@Override
	public void exitMoves(GTPParser.MovesContext ctx) {
		LOG.trace("Creating moves for: {}", ctx.getText());
		var moves = new LinkedList<Move>();
		for (var move : ctx.move()) {
			moves.add(pop(Move.class));
		}
		push(moves);
	}

	@Override
	public void exitNormalMove(GTPParser.NormalMoveContext ctx) {
		LOG.trace("Creating normal move for: {}", ctx.getText());
		var side = pop(Side.class);
		push(new Move(side.stone, Vertex.parse(ctx.VERTEX().getText())));
	}

	@Override
	public void exitBlockMove(cz.doleckovi.piskvorky.gtp.parser.GTPParser.BlockMoveContext ctx) {
		LOG.trace("Creating block move for: {}", ctx.getText());
		push(new Move(Stone.BLOCK, Vertex.parse(ctx.VERTEX().getText())));
	}

	@Override
    public void exitWhiteColor(cz.doleckovi.piskvorky.gtp.parser.GTPParser.WhiteColorContext ctx) {
		LOG.trace("Creating white side for: {}", ctx.getText());
		stack.push(Side.WHITE);
    }

    @Override
    public void exitBlackColor(cz.doleckovi.piskvorky.gtp.parser.GTPParser.BlackColorContext ctx) {
	    LOG.trace("Creating black side for: {}", ctx.getText());
		stack.push(Side.BLACK);
    }

}
