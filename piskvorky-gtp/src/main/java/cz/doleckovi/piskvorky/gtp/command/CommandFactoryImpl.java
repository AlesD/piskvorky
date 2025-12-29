package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.List;
import java.util.Map;

public class CommandFactoryImpl implements CommandFactory {

	private final ListableBeanFactory beanFactory;
	private final Map<String, String> commandNames;

	public CommandFactoryImpl(ListableBeanFactory beanFactory, Map<String, String> commandNames) {
		this.beanFactory = beanFactory;
		this.commandNames = Map.copyOf(commandNames);
	}

	private String getBeanName(String commandName) {
		var result = commandNames.get(commandName);
		if (result == null)
			throw new IllegalArgumentException("Unknown command %s".formatted(commandName));
		return result;
	}

	@Override
	public Command createSimpleCommand(String commandName) {
		return beanFactory.getBean(getBeanName(commandName), Command.class);
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

	@Override
	public Command createSideCommand(String commandName, Side side) {
		return (Command) beanFactory.getBean(getBeanName(commandName), side);
//                    | 'genmove' COLOR                      # ColorCommand
//                    | 'reg_genmove' COLOR                  # ColorCommand
                    // GNU Go Extensions
//                    | 'list_stones' COLOR                  # ColorCommand
//                    | 'all_legal' COLOR                    # ColorCommand
                    // Go GUI extensions
//                    | 'gogui-setup_player' COLOR           # ColorCommand
//                    // piskvorky extensions
	}

	@Override
	public Command createTextCommand(String commandName, String text) {
		return (Command) beanFactory.getBean(getBeanName(commandName), text);
//                    // GNU Go Extensions
//                    | 'echo_err' string=TEXT               # IntCommand
    }

	@Override
    public Command createNumberCommand(String commandName, Integer integer) {
		return (Command) beanFactory.getBean(getBeanName(commandName), integer);
//                    // GNU Go Extensions
//                    | 'gg_undo' INTEGER?                   # IntCommand
//                    | 'orientation' INTEGER                # IntCommand
//                    // Go GUI extensions
//                    | 'gogui-action_forward' INTEGER?      # IntCommand
//                    | 'gogui-action_backward' INTEGER?     # IntCommand
//                    // piskvorky extensions
    }

/*
    // GNU Go Extensions
    // Go GUI extensions
    // piskvorky extensions
    | 'piskvorky-config' key=ID value=TEXT # KVPCommand
 */

	@Override
    public Command createVertexCommand(String commandName, Vertex vertex) {
		return (Command) beanFactory.getBean(getBeanName(commandName), vertex);
            // GNU Go Extensions
//    | 'color' VERTEX                       # VertexCommand
    }

	@Override
    public Command createMoveCommand(String commandName, List<Move> moves) {
		return (Command) beanFactory.getBean(getBeanName(commandName), moves);
/*
//                    | 'play' move                          # MoveCommand
//                    // GNU Go Extensions
//                    | 'is_legal' move                      # MoveCommand
//                    // Go GUI extensions
    // Go GUI extensions
    | 'gogui-setup' moves                  # MoveListCommand
    | 'gogui-play_sequence' moves          # MoveListCommand
 */
    }

	@Override
    public Command createKVPCommand(String commandName, String key, String value) {
	    return (Command) beanFactory.getBean(getBeanName(commandName), key, value);
/*
                // piskvorky extensions
    | 'piskvorky-config' key=ID value=TEXT # KVPCommand
 */
    }

}
