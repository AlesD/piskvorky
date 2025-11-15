parser grammar GTPParser;

options { tokenVocab=GTPLexer; }

action
	: id=INTEGER? command # CommandWithId
	| INTERRUPT           # InterruptAction
	| EOF                 # NoOpAction
	;

command
    : COMMAND TEXT           # TextCommand
    | COMMAND KEY TEXT       # KVPCommand
    | COMMAND moves          # MovesCommand
    | COMMAND move           # MoveCommand
    | COMMAND VERTEX         # VertexCommand
    | COMMAND side           # SideCommand
    | COMMAND INTEGER        # NumberCommand
    | COMMAND                # SimpleCommand
    ;

moves
    : move+
    ;

move
    : side VERTEX  # NormalMove
    | BLOCK VERTEX # BlockMove
    ;

side
    : WHITE # WhiteColor
    | BLACK # BlackColor
    ;