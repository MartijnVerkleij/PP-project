grammar Grammar;

@header{package grammar;}

program	: stat+;

stat	: GLOBAL? type ID SEMI 									#declStat
		| GLOBAL? type ID ASS expr SEMI							#declAssStat
		| ID ASS expr SEMI 										#assStat
		| ENUM ID ASS LBRACE EID (COMMA EID)* RBRACE SEMI		#enumStat
		| IF LPAR expr RPAR stat* (ELSE stat*)?					#ifStat
		| WHILE LPAR expr RPAR stat*							#whileStat
		| block													#blockStat
		| type ID LPAR (type ID (COMMA type ID)*)? RPAR block	#funcDecl
		| expr SEMI												#exprStat
		| RUN ID LPAR (expr (COMMA expr)*)? RPAR SEMI			#runStat
		;

block 	: LBRACE stat* RBRACE
		;

type	: INT													#intType
		| BOOL													#boolType
		;
		
expr	: ID LPAR (expr (COMMA expr)*)? RPAR					#funcCall
		| JOIN ID												#join
		| expr plusOp expr										#plusExpr
		| expr multOp expr										#multExpr
		| expr expOp expr										#expExpr
		| expr boolOp expr										#boolExpr
		| expr cmpOp expr										#cmpExpr
		| prfOp expr											#prfExpr
		| LPAR expr RPAR 										#parExpr
		| ID													#idExpr
		| NUM													#numExpr
		| EID													#eidExpr
		| TRUE													#trueExpr
		| FALSE													#falseExpr
		;
		
plusOp	: PLUS | MINUS;
multOp	: STAR | SLASH;
expOp	: CARET;
boolOp	: AND | OR;
cmpOp	: EQ | GT | GE | LT | LE | NE;
prfOp	: MINUS | NOT;


GLOBAL: G L O B A L;
INT: 	I N T;
BOOL: 	B O O L;
ENUM: 	E N U M;
IF: 	I F;
ELSE: 	E L S E;
WHILE: 	W H I L E;
RUN:	R U N;
JOIN:	J O I N;

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
