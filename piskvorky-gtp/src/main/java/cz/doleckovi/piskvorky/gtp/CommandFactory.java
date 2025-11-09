package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;

public class CommandFactory {

    Command createCommand(Integer commandId, String commandName) {
        switch (commandName) {
//                    : 'protocol_version'
//                    | 'name'
//                    | 'version'
//                    | 'list_commands'
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
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    Command createCommand(Integer commandId, String commandName, Side side) {
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

    Command createCommand(Integer commandId, String commandName, String text) {
        switch (commandName) {
//                    | 'known_command' string=ID            # StringCommand
//                    // GNU Go Extensions
//                    | 'echo' string=TEXT                   # IntCommand
//                    | 'echo_err' string=TEXT               # IntCommand
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

    Command createCommand(Integer commandId, String commandName, Integer integer) {
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
    | 'play' move                          # MoveCommand
    // GNU Go Extensions
    | 'color' VERTEX                       # VertexCommand
    | 'is_legal' move                      # MoveCommand
    // Go GUI extensions
    | 'gogui-setup' moves                  # MoveListCommand
    | 'gogui-play_sequence' moves          # MoveListCommand
    // piskvorky extensions
    | 'piskvorky-config' key=ID value=TEXT # KVPCommand
 */


    Command createCommand(Integer commandId, String commandName, Side side, Vertex vertex) {
        switch (commandName) {
//                    | 'play' move                          # MoveCommand
//                    // GNU Go Extensions
//                    | 'is_legal' move                      # MoveCommand
//                    // Go GUI extensions
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }
    Command createCommand(Integer commandId, String commandName, String text) {
        switch (commandName) {
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }
    Command createCommand(Integer commandId, String commandName, String text) {
        switch (commandName) {
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }
    Command createCommand(Integer commandId, String commandName, String text) {
        switch (commandName) {
            default -> throw new IllegalArgumentException("Unknown command: %s".formatted(commandName));
        }
    }

}
