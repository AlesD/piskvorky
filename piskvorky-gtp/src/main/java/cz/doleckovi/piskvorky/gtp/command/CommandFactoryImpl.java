package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommandFactoryImpl implements CommandFactory {

	private static final Logger LOGGER = Logger.getLogger(CommandFactoryImpl.class.getSimpleName());
	static final Map<String, Class<? extends GTPCommand>> ALL_COMMANDS = new LinkedHashMap<>();

	static {
		ALL_COMMANDS.put(ProtocolVersionCommand.NAME, ProtocolVersionCommand.class);
		ALL_COMMANDS.put(NameCommand.NAME, NameCommand.class);
		ALL_COMMANDS.put(VersionCommand.NAME, VersionCommand.class);
		ALL_COMMANDS.put(KnownCommandCommand.NAME, KnownCommandCommand.class);
		ALL_COMMANDS.put(ListCommandsCommand.NAME, ListCommandsCommand.class);
		// GNU Go Extensions
		ALL_COMMANDS.put(EchoCommand.NAME, EchoCommand.class);
	}

	private final NameCommand nameCommand = NameCommand.INSTANCE;
	private final VersionCommand versionCommand = new VersionCommand();

    public GTPCommand createSimpleCommand(String commandName) {
		return switch (commandName) {
			case ProtocolVersionCommand.NAME -> ProtocolVersionCommand.INSTANCE;
			case NameCommand.NAME -> nameCommand;
			case VersionCommand.NAME -> versionCommand;
			default -> throw new IllegalArgumentException();
		};
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

    public GTPCommand createColorCommand(String commandName, Side side) {
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

    public GTPCommand createTextCommand(String commandName, String text) {
        return switch (commandName) {
	        case KnownCommandCommand.NAME -> new KnownCommandCommand(text);
	        case EchoCommand.NAME -> new EchoCommand(text);
//                    // GNU Go Extensions
//                    | 'echo_err' string=TEXT               # IntCommand
	        default -> throw new IllegalArgumentException();
        };
    }

    public GTPCommand createNumberCommand(String commandName, String integer) {
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


    public GTPCommand createMoveCommand(String commandName, Move move) {
        switch (commandName) {
//                    | 'play' move                          # MoveCommand
//                    // GNU Go Extensions
//                    | 'is_legal' move                      # MoveCommand
//                    // Go GUI extensions
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    public GTPCommand createVertexCommand(String commandName, Vertex vertex) {
        switch (commandName) {
            // GNU Go Extensions
//    | 'color' VERTEX                       # VertexCommand
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    public GTPCommand createMovesCommand(String commandName, List<Move> moves) {
        switch (commandName) {
/*
    // Go GUI extensions
    | 'gogui-setup' moves                  # MoveListCommand
    | 'gogui-play_sequence' moves          # MoveListCommand
 */
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    public GTPCommand createKVPCommand(String commandName, String key, String value) {
        switch (commandName) {
/*
                // piskvorky extensions
    | 'piskvorky-config' key=ID value=TEXT # KVPCommand
 */
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

}
