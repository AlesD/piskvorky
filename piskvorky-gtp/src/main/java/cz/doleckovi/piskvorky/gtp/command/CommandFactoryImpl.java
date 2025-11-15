package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.*;

import static java.util.Objects.*;

public class CommandFactoryImpl implements CommandFactory {

	private static final Logger LOG = LoggerFactory.getLogger(CommandFactoryImpl.class);

	private static final Map<String, Class<?>> ALL_COMMANDS = new LinkedHashMap<>();

	static {
		addCommand(ProtocolVersionCommand.class);
		addCommand(NameCommand.class);
		addCommand(VersionCommand.class);
		addCommand(KnownCommandCommand.class);
		addCommand(ListCommandsCommand.class);
		// Setup commands
		addCommand(BoardSizeCommand.class);
		// GNU Go Extensions
		addCommand(EchoCommand.class);
	}

	static void addCommand(Class<?> type) {
		requireNonNull(type, "Parameter type is null");
		if (Command.class.isAssignableFrom(type)) {
			LOG.trace("Adding command {}", type);
		} else if (Action.class.isAssignableFrom(type)) {
			LOG.trace("Adding action {}", type);
		} else {
			throw new IllegalArgumentException("Class %s is neither action nor command".formatted(type));
		}
		var name = getName(type);
		ALL_COMMANDS.put(name, type);
		LOG.debug("Added {} as {}", type, name);
	}

	static String getName(Class<?> type) {
		var commandInfo = type.getAnnotation(CommandInfo.class);
		if (commandInfo == null)
			throw new IllegalArgumentException("Class %s is not command".formatted(type));
		return commandInfo.name();
	}

	private final ListableBeanFactory beanFactory;
	private final Map<String, Class<?>> commandClasses = new HashMap<>();
	private final Map<String, Object> commandBeans = new HashMap<>();

	public CommandFactoryImpl(ListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		for (var beanName : beanFactory.getBeanNamesForAnnotation(CommandInfo.class)) {
			var beanType = beanFactory.getType(beanName);
			var commandInfo = beanType.getAnnotation(CommandInfo.class);
			var commandName = commandInfo.name();
			if (beanFactory.isSingleton(beanName)) {
				LOG.info("Command {} implemented by singleton bean {} of type {}", commandName, beanName, commandClasses);
				commandBeans.put(commandName, beanFactory.getBean(beanName));
			} else if (beanFactory.isPrototype(beanName)) {
				LOG.info("Command {} implemented by prototype bean {} of type {}", commandName, beanName, commandClasses);
				commandClasses.put(commandName, beanType);
			}
		}
	}

	public Object createSimpleCommand(String commandName) {
		if (commandBeans.containsKey(commandName))
			return commandBeans.get(commandName);
		if (commandClasses.containsKey(commandName))
			return beanFactory.getBean(commandClasses.get(commandName));
		if (beanFactory.containsBean(commandName))
			return beanFactory.getBean(commandName);
		if (beanFactory.containsBeanDefinition(commandName))
			return beanFactory.getBean(commandName);
		return new ExceptionAction("unknown command",
				new IllegalArgumentException("Unknown command %s".formatted(commandName)));

//                    | 'help'
//                    | 'quit'
//                    | 'clear_board'
//                    | 'undo'
//                    | 'showboard'
//                    // GNU Go Extensions
//                    | 'query_boardsize'
//                    | 'query_orientation'
//                    | 'last_move'
//                    | 'move_history'
//                    | 'increase_depths'
//                    | 'decrease_depths'
//                    | 'restricted_genmove'
//                    | 'estimate_score'
//                    | 'experimenal_score'
//                    | 'cputime'
//                    | 'move_probabilities'
//                    // Go GUI extensions
//                    | 'gogui-title'
//                    | 'gogui-rules_game_id'
//                    | 'gogui-rules_board'
//                    | 'gogui-rules_game_gfx'
//                    | 'gogui-rules_captured_count'
//                    | 'gogui-rules_board_size'
//                    | 'gogui-rules_legal_moves'
//                    | 'gogui-rules_side_to_move'
//                    | 'gogui-rules_final_result'
//                    | 'gogui-analyze_commands'
//                    // piskvorky extensions
//                    | 'piskvorky-cell_data'
//                    | 'piskvorky-field_data'
//                    | 'piskvorky-line_gfx'
//                    | 'piskvorky-field_gfx'
    }

    public Command createSideCommand(String commandName, Side side) {
        switch (commandName) {
//                    | 'genmove' COLOR                      # ColorCommand
//                    | 'reg_genmove' COLOR                  # ColorCommand
                    // GNU Go Extensions
//                    | 'list_stones' COLOR                  # ColorCommand
//                    | 'all_legal' COLOR                    # ColorCommand
                    // Go GUI extensions
//                    | 'gogui-setup_player' COLOR           # ColorCommand
//                    // piskvorky extensions
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    public Command createTextCommand(String commandName, String text) {
        return switch (commandName) {
	        case KnownCommandCommand.NAME -> new KnownCommandCommand(text);
	        case EchoCommand.NAME -> new EchoCommand(text);
//                    // GNU Go Extensions
//                    | 'echo_err' string=TEXT               # IntCommand
	        default -> throw new IllegalArgumentException();
        };
    }

    public Command createNumberCommand(String commandName, String integer) {
        switch (commandName) {
//                    | 'board_size' INTEGER                 # IntCommand
//                    // GNU Go Extensions
//                    | 'gg_undo' INTEGER?                   # IntCommand
//                    | 'orientation' INTEGER                # IntCommand
//                    // Go GUI extensions
//                    | 'gogui-action_forward' INTEGER?      # IntCommand
//                    | 'gogui-action_backward' INTEGER?     # IntCommand
//                    // piskvorky extensions
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

/*
    // GNU Go Extensions
    // Go GUI extensions
    // piskvorky extensions
    | 'piskvorky-config' key=ID value=TEXT # KVPCommand
 */


    public Command createMoveCommand(String commandName, Move move) {
        switch (commandName) {
//                    | 'play' move                          # MoveCommand
//                    // GNU Go Extensions
//                    | 'is_legal' move                      # MoveCommand
//                    // Go GUI extensions
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    public Command createVertexCommand(String commandName, Vertex vertex) {
        switch (commandName) {
            // GNU Go Extensions
//    | 'color' VERTEX                       # VertexCommand
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    public Command createMovesCommand(String commandName, List<Move> moves) {
        switch (commandName) {
/*
    // Go GUI extensions
    | 'gogui-setup' moves                  # MoveListCommand
    | 'gogui-play_sequence' moves          # MoveListCommand
 */
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    public Command createKVPCommand(String commandName, String key, String value) {
        switch (commandName) {
/*
                // piskvorky extensions
    | 'piskvorky-config' key=ID value=TEXT # KVPCommand
 */
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

}
