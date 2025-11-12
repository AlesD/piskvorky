parser grammar GTPParser;

options { tokenVocab=GTPLexer; }

@header {
import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;
}

@members {
CommandFactory factory;
}

action returns [ Command value ]
    : INTERRUPT # InterruptAction
    | command   # CommandAction
    ;                                      
                                           
command returns [ GTPCommand value ]
    : COMMAND TEXT           # TextCommand
    | COMMAND KEY TEXT       # KVPCommand
    | COMMAND moves          # MovesCommand
    | COMMAND move           # MoveCommand
    | COMMAND VERTEX         # VertexCommand
    | COMMAND color          # ColorCommand
    | COMMAND number=INTEGER # NumberCommand
    | COMMAND                # SimpleCommand
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