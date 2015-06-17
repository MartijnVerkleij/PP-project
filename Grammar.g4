grammar Grammar;

program	: stat+;

stat	: type ID SEMI 
		| type ID ASS expr SEMI
		| ID ASS expr SEMI 
		| ENUM ID ASS LBRACE EID (COMMA EID)* RBRACE SEMI
		| IF LPAR expr RPAR stat* (ELSE stat*)?
		| WHILE LPAR expr RPAR stat*
		| LBRACE stat* RBRACE
		;

type	: INT
		| BOOL
		;
		
expr	: expr plusOp expr
		| expr multOp expr
		| expr expOp expr
		| expr boolOp expr
		| expr cmpOp expr
		| prfOp expr
		| LPAR expr RPAR 
		| ID
		| NUM
		| EID
		| TRUE
		| FALSE
		;
		
plusOp	: PLUS | MINUS;
multOp	: STAR | SLASH;
expOp	: CARET;
boolOp	: AND | OR;
cmpOp	: EQ | GT | GE | LT | LE | NE;
prfOp	: MINUS | NOT;


INT: 	I N T;
BOOL: 	B O O L;
ENUM: 	E N U M;
IF: 	I F;
ELSE: 	E L S E;
WHILE: 	W H I L E;

TRUE:	T R U E;
FALSE:	F A L S E;




ASS: 	'=';

EQ: 	'==';
GT: 	'>';
GE: 	'>=';
LT:     '<';
LE:     '<=';
NE:     '!=';

AND:	'&&';
OR:		'||';
NOT:	'!';

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
CARET:	'^';
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
