parser grammar GTPParser;

options { tokenVocab=GTPLexer; }

@header {
import cz.doleckovi.piskvorky.api;
}

@members {
CommandFactory factory;
}

action
    : INTERRUPT
    | command
    ;                                      
                                           
command returns [ GTPCommand cmd; ]
    : id=INTEGER? COMMAND TEXT { $cmd = factory.createCommand( $id == null ? $id.int : null, COMMAND.text, TEXT.text); }
    : id=INTEGER? COMMAND { $cmd = factory.createCommand( $id == null ? $id.int : null, COMMAND.text); }
    ;

moves: move+;

color returns[ Side: side; ]
    : WHITE { $side = Side.WHITE; }
    | BLACK { $side = Side.BLACK; }
    ;

move returns[ Stone: stone; Vertex: vertex; ]
    : color VERTEX { $stone = $color.side.stone(); $vertex = Vertex.of($VERTEX.text); }
    | BLOCK VERTEX { $stone = Stone.BLOCK; $vertex = Vertex.of($VERTEX.text); }
    ;