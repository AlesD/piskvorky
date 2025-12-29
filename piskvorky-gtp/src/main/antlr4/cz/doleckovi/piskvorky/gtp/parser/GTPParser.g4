parser grammar GTPParser;

options { tokenVocab=GTPLexer; }

action
	: id=INTEGER? command # CommandWithId
	| INTERRUPT           # InterruptAction
	| EOL                 # EndOfLine
	;

command
    : COMMAND text           # TextCommand
    | COMMAND KEY text       # KVPCommand
    | COMMAND moves          # MoveCommand
    | COMMAND VERTEX         # VertexCommand
    | COMMAND side           # SideCommand
    | COMMAND INTEGER        # NumberCommand
    | COMMAND                # SimpleCommand
    ;

moves
    : move       # CreateMoves
    | moves move # AddMove
    ;

move
    : side VERTEX  # NormalMove
    | BLOCK VERTEX # BlockMove
    ;

side
    : WHITE # WhiteColor
    | BLACK # BlackColor
    ;

text: WORD+;