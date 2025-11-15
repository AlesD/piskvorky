lexer grammar GTPLexer;

fragment INT: '0' | [1-9] [0-9]*;
fragment WS: [ \t];
fragment COMMENT: '#' ~'\n'*;
fragment ID_START: [a-zA-Z];
fragment ID_CONTINUATION: ID_START | [0-9_];
fragment ID: ID_START ID_CONTINUATION*;


// Special cases first

TEXT_COMMAND: ( 'echo' | 'echo_err' | 'known_command') -> type(COMMAND), mode(TEXT_MODE);
KVP_COMMAND: 'piskvorky-config' -> type(COMMAND), mode(KVP_MODE);

INTEGER: INT;
COMMAND: (ID '-')? ID -> mode(ARG_MODE);
INTERRUPT: '# interrupt'; // Special kinf of comment

IGNORED: (WS | COMMENT) -> skip;

mode ARG_MODE;

VERTEX options { caseInsensitive=true; } : [a-hj-z] INT;
WHITE options { caseInsensitive=true; }: 'w' 'hite'?;
BLACK options { caseInsensitive=true; }: 'b' 'lack'?;
BLOCK options { caseInsensitive=true; }: 'block';
ARG_IGNORED: (WS | COMMENT) -> skip;

mode KVP_MODE;

KEY: [a-z][a-z0-9_-]* -> mode(TEXT_MODE);
KVP_INTERRUPT: INTERRUPT -> type(INTERRUPT);
KVP_IGNORED: (WS | COMMENT) -> skip;

mode TEXT_MODE;

TEXT_IGNORED: (WS | COMMENT) -> skip; // Goes first to remove leading WS
TEXT: ~'#'+;
