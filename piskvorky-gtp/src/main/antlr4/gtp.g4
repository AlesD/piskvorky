grammar gtp;

fragment DIGIT: [0-9];
fragment SIGN: [+-];
fragment INT: '0' | [1-9] DIGIT*;
fragment EXPONENT: [eE] SIGN? DIGIT+;
fragment LETTER: [a-hj-zA-HJ-Z];

EOL: '\n' -> skip;
WS: [ \t] -> skip;

CONTROL: [\p{General_Category=Control}] -> skip;
OTHER_WHITE_SPACE: [\p{Pattern_White_Space}] -> skip;

INTERUPT: '# interupt' ;
COMMENT: '#' ~[\n]* -> skip;

BOOLEAN : 'true' | 'false';

INTEGER: SIGN? INT;

FLOAT: SIGN? INT '.' DIGIT* EXPONENT?
     | '.' DIGIT+ EXPONENT?
     | SIGN? INT EXPONENT
     ;

VERTEX: LETTER [1-9] DIGIT?;

COLOR options { caseInsensitive=true; }
    : 'w'
    | 'white'
    | 'b'
    | 'black';

DIRECTION options { caseInsensitive=true; }
    : 'h'
    |'horizontal'
    | 'v'
    | 'vertical'
    | 'u'
    | 'uphill'
    | 'd'
    | 'downhill'
    ;

STRING: [\P{White_Space}]+;

move: COLOR VERTEX;

word
    : STRING
    | BOOLEAN
    | INTEGER
    | FLOAT
    | VERTEX
    | COLOR
    | DIRECTION
    ;

text
    : word
    | text word
    ;

input: command EOF;

command
    : gtp_command
    | gnu_go_extension_command
    | gogui_extension_commmand
    ;

gtp_command
    : administrative_command
    | setup_command
    | play_command
    | regression_command
    | debug_command
    ;

administrative_command
    : 'protocol_version'
    | 'name'
    | 'version'
    | 'know_command' name=STRING
    | 'list_commands'
    | 'help'
    | 'quit'
    ;

setup_command
    : 'board_size' size=INTEGER
    | 'clear_board'
    ;

play_command
    : 'play' move
    | 'genmove' COLOR
    | 'undo'
    ;

regression_command
    : 'reg_genmove' COLOR
    ;

debug_command
    : 'showboard'
    ;

gnu_go_extension_command
    : 'gg_undo' INTEGER?
    | 'orientation' INTEGER
    | 'query_boardsize'
    | 'query_orientation'
    | 'color' VERTEX
    | 'list_stones' COLOR
    | 'is_legal' move
    | 'all_legal' COLOR
    | 'last_move'
    | 'move_history'
    | 'increase_depths'
    | 'decrease_depths'
    | 'restricted_genmove'
    | 'estimate_score'
    | 'experimenal_score'
    | 'cputime'
    | 'move_probabilities'
    | 'echo' text
    | 'echo_err' text
    ;

gogui_extension_commmand
    : 'gogui-title'
    | gogui_rules_command
    | gogui_action_command
    | gogui_interupting_command
    | gogui_analyze_command
    | gogui_setup_command
    ;

gogui_rules_command
    : 'gogui-rules_game_id'
    | 'gogui-rules_board'
    | 'gogui-rules_game_gfx'
    | 'gogui-rules_captured_count'
    | 'gogui-rules_board_size'
    | 'gogui-rules_legal_moves'
    | 'gogui-rules_side_to_move'
    | 'gogui-rules_final_result'
    ;

gogui_action_command
    : 'gogui-action_forward' INTEGER?
    | 'gogui-action_backward' INTEGER?
    | 'gogui-play_sequence' moves=move+
    ;

gogui_interupting_command
    : INTERUPT
    ;

gogui_analyze_command
    : 'gogui-analyze_commands'
    | 'piskvorky-config' key=STRING value=text
    | 'piskvorky-cell_data'
    | 'piskvorky-field_data'
    | 'piskvorky-line_gfx'
    | 'piskvorky-field_gfx'
    ;

gogui_setup_command
    : 'gogui-setup' moves=move+
    | 'gogui-setup_player' COLOR
    ;