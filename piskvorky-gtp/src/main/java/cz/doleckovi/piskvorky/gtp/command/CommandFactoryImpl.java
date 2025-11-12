package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.GTPCommand;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.util.List;

public class CommandFactoryImpl implements CommandFactory {

    private final BeanFactory beanFactory;

    public CommandFactoryImpl(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public GTPCommand createSimpleCommand(String commandName) {
        return beanFactory.getBean(commandName, GTPCommand.class);
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
            case "known_command" -> beanFactory.getBean(KnownCommandCommand.class, text);
            case "echo" -> beanFactory.getBean(EchoCommand.class, text);
//                    // GNU Go Extensions
//                    | 'echo_err' string=TEXT               # IntCommand
            default -> (GTPCommand) beanFactory.getBean(commandName, text);
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
