parser grammar GTPParser;

options { tokenVocab=GTPLexer; }

// Per-line entry point is parser.line(); input stays for whole-file validation
// only - no execution semantics may hang off it. Feed file content through a
// trailing-newline wrapper so the last line always ends with '\n' (never rely
// on files ending in one).
// INVARIANT: every decision in this grammar is resolved without looking past an
// EOL - so parsing one line at a time sees the same token context as the full
// stream. A future rule that needs tokens AFTER its EOL would break that.
input
	: line* EOF
	;

line
	: id=INTEGER? command EOL # CommandLine
	| INTERRUPT EOL           # InterruptLine
	| EOL                     # EmptyLine
	;

command
    : COMMAND                # SimpleCommand
    | COMMAND INTEGER        # NumberCommand
    | COMMAND VERTEX         # VertexCommand
    | COMMAND side           # SideCommand
    | COMMAND moves          # MoveCommand
    | COMMAND KEY text       # KVPCommand
    | COMMAND text           # TextCommand
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

text
    : WORD+ # AddWords
    ;