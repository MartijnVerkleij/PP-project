grammar Grammar;

program	: stat+;

stat	:  IF LBRACE expr RBRACE stat;
		



ASS: 	'=';

EQ: 	'==';
GT: 	'>';
GE: 	'>=';
LT:     '<';
LE:     '<=';
NE:     '!=';
AND:	'&&';
OR:		'||';

LBRACE: '{';
RBRACE: '}';
LPAR:   '(';
RPAR:   ')';
LBRACK:	'[';
RBRACK:	']';
SEMI:   ';';
DQUOTE: '"';
SQUOTE: '\'';
COLON:  ':';
COMMA:  ',';
DOT:    '.';
AMP:	'&';

PLUS:   '+';
MINUS:  '-';
SLASH:  '/';
STAR:   '*';
UND:	'_';

COMMENT: '/*'.*? '*/' -> skip;
WS: [ \t\r\n]+ -> skip;

ID: 	LCASEL (LETTER | UND | DIGIT)*;
NUM:	DIGIT+;
EID:	UCASEL (UCASEL | UND | DIGIT)*;
STR: 	DQUOTE .*? DQUOTE;


fragment LETTER: [a-zA-Z];
fragment UCASEL: [A-Z];
fragment LCASEL: [a-z];
fragment DIGIT: [0-9];

fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];