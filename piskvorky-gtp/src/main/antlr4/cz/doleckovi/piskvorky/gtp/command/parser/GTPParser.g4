parser grammar GTPParser;

options { tokenVocab=GTPLexer; }

@header {
import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.gtp.Command;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
}

@members {
CommandFactory factory;
}

action returns [ Command value ]
	: INTEGER? command
	;

command returns [ Command value ]
    : COMMAND TEXT           # TextCommand
    | COMMAND KEY TEXT       # KVPCommand
    | COMMAND moves          # MovesCommand
    | COMMAND move           # MoveCommand
    | COMMAND VERTEX         # VertexCommand
    | COMMAND color          # ColorCommand
    | COMMAND number=INTEGER # NumberCommand
    | COMMAND                # SimpleCommand
    | INTERRUPT              # InterruptCommand
    | EOF                    # EmptyCommand
    ;

moves returns [ List<Move> value ]
    : move+
    ;

move returns[ Move value ]
    : color VERTEX # NormalMove
    | BLOCK VERTEX # BlockMove
    ;

color returns[ Side value ]
    : WHITE # WhiteColor
    | BLACK # BlackColor
    ;