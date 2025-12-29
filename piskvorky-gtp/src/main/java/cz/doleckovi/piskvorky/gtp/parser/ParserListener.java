package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.trait.Traits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class ParserListener extends GTPParserBaseListener {

	private static final Logger LOG = LoggerFactory.getLogger(ParserListener.class);

	private final Deque<Object> stack = new LinkedList<>();
	private final CommandFactory factory;
	private final Consumer<Command> commandConsumer;

    public ParserListener(CommandFactory factory, Consumer<Command> commandConsumer) {
        this.factory = requireNonNull(factory, "Parameter 'factory' is null");
		this.commandConsumer = commandConsumer;
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
		var command = pop(Command.class);
		if (ctx.id != null) {
			var id = ctx.id.getText();
			LOG.trace("Adding ID {} to {}", id, command);
			command = (Command) Traits.addId(command, id);
		}
		commandConsumer.accept(command);
	}

	@Override
	public void exitInterruptAction(GTPParser.InterruptActionContext ctx) {
		LOG.trace("Creating interrupt action");
		commandConsumer.accept(factory.createInterruptAction());
	}

	@Override
	public void exitEndOfLine(GTPParser.EndOfLineContext ctx) {
		LOG.trace("End of line");
		stack.clear();
	}

	@Override
	public void exitTextCommand(GTPParser.TextCommandContext ctx) {
		var commandName = ctx.COMMAND().getText();
		var text = pop(String.class);
		LOG.trace("Creating command {} for text {}", commandName, text);
		push(factory.createTextCommand(commandName, text));
	}

	@Override
	public void exitKVPCommand(GTPParser.KVPCommandContext ctx) {
		var commandName = ctx.COMMAND().getText();
		var key = ctx.KEY().getText();
		var text = pop(String.class);
		LOG.trace("Creating command {} for key {} and value {}", commandName, key, text);
		push(factory.createKVPCommand(commandName, key, text));
	}

	@Override
	public void exitMoveCommand(GTPParser.MoveCommandContext ctx) {
		var commandName = ctx.COMMAND().getText();
		@SuppressWarnings("unchecked")
		var moves = (List<Move>) pop(List.class);
		LOG.trace("Creating command {} for moves {}", commandName, moves);
		push(factory.createMoveCommand(commandName, moves));
	}

	@Override
	public void exitVertexCommand(GTPParser.VertexCommandContext ctx) {
		var commandName = ctx.COMMAND().getText();
		var vertex = ctx.VERTEX().getText();
		LOG.trace("Creating command {} for vertex {}", commandName, vertex);
		push(factory.createVertexCommand(commandName, Vertex.parse(vertex)));
	}

	@Override
	public void exitSideCommand(GTPParser.SideCommandContext ctx) {
		var commandName = ctx.COMMAND().getText();
		var side = pop(Side.class);
		LOG.trace("Creating command {} for side {}", commandName, side);
		push(factory.createSideCommand(commandName, side));
	}

	@Override
	public void exitNumberCommand(GTPParser.NumberCommandContext ctx) {
		var commandName = ctx.COMMAND().getText();
		var number = ctx.INTEGER().getText();
		LOG.trace("Creating command {} for number {}", commandName, number);
		push(factory.createNumberCommand(commandName, Integer.parseInt(number)));
	}

	@Override
	public void exitSimpleCommand(GTPParser.SimpleCommandContext ctx) {
		var commandName = ctx.COMMAND().getText();
		LOG.trace("Creating command {}", commandName);
		push(factory.createSimpleCommand(commandName));
	}

	@Override
	public void exitCreateMoves(GTPParser.CreateMovesContext ctx) {
		var move = pop(Move.class);
		LOG.trace("Creating moves from: {}", move);
		var moves = new LinkedList<Move>();
		moves.add(move);
		push(moves);
	}

	@Override
	public void exitAddMove(GTPParser.AddMoveContext ctx) {
		var move = pop(Move.class);
		LOG.trace("Adding move: {}", move);
		@SuppressWarnings("unchecked")
		var moves = (List<Move>) pop(List.class);
		moves.add(move);
		push(moves);
	}

	@Override
	public void exitNormalMove(GTPParser.NormalMoveContext ctx) {
		var vertex = ctx.VERTEX().getText();
		LOG.trace("Creating normal move for: {}", vertex);
		var side = pop(Side.class);
		push(new Move(side.stone, Vertex.parse(vertex)));
	}

	@Override
	public void exitBlockMove(cz.doleckovi.piskvorky.gtp.parser.GTPParser.BlockMoveContext ctx) {
		var vertex = ctx.VERTEX().getText();
		LOG.trace("Creating block move for: {}", vertex);
		push(new Move(Stone.BLOCK, Vertex.parse(vertex)));
	}

	@Override
    public void exitWhiteColor(cz.doleckovi.piskvorky.gtp.parser.GTPParser.WhiteColorContext ctx) {
		LOG.trace("Creating white side for: {}", ctx.getText());
		push(Side.WHITE);
    }

    @Override
    public void exitBlackColor(cz.doleckovi.piskvorky.gtp.parser.GTPParser.BlackColorContext ctx) {
	    LOG.trace("Creating black side for: {}", ctx.getText());
		push(Side.BLACK);
    }

	@Override
	public void exitText(GTPParser.TextContext ctx) {
		LOG.trace("Got text: {}", ctx.getText());
		push(ctx.getText());
	}
}
