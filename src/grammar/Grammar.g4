grammar Grammar;

@header{package grammar;}

// Program consists of one or more statements
program	: stat+;

/* Statments
* 	declStat	- Declaration and Optional Assignment
*		Declare a (optionally global) variable and optionally assign an expression to it
*	assStat		- Assignment
*		Assign an expression to a variable
*	enumStat	- Enum Declaration
*
*	ifStat		- If
*	whileStat	- While
*	blockStat	- Block
*	funcStat	- Function Declaration
*	exprStat	- Expression
*	runStat		- Run
*	lockStat	- Lock
*	unlockStat	- Unlock
*	returnStat	- Return
*/
stat	: GLOBAL? type ID (ASS expr)? SEMI							#declStat
		| ID ASS expr SEMI 											#assStat
		| ENUM ID ASS LBRACE EID (COMMA EID)* RBRACE SEMI			#enumStat
		| IF LPAR expr RPAR stat* (ELSE stat*)?						#ifStat
		| WHILE LPAR expr RPAR stat*								#whileStat
		| block														#blockStat
		| type ID LPAR (type ID (COMMA type ID)*)? RPAR block		#funcStat
		| expr SEMI													#exprStat
		| RUN ID LPAR (ID ((COMMA expr)*)?) RPAR SEMI				#runStat
		| LOCK ID SEMI												#lockStat
		| UNLOCK ID SEMI											#unlockStat
		| RETURN expr SEMI											#returnStat
		;

block 	: LBRACE stat* RBRACE
		;

type	: INT														#intType
		| BOOL														#boolType
		| VOID														#voidType
		;
		
expr	: ID LPAR (expr (COMMA expr)*)? RPAR						#funcCall
		| JOIN ID													#join
		| LOCKED ID													#lockedExpr
		| expr plusOp expr											#plusExpr
		| expr multOp expr											#multExpr
		| expr expOp expr											#expExpr
		| expr boolOp expr											#boolExpr
		| expr cmpOp expr											#cmpExpr
		| prfOp expr												#prfExpr
		| LPAR expr RPAR 											#parExpr
		| ID														#idExpr
		| NUM														#numExpr
		| EID														#eidExpr
		| TRUE														#trueExpr
		| FALSE														#falseExpr
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
VOID:	V O I D;
ENUM: 	E N U M;
IF: 	I F;
ELSE: 	E L S E;
WHILE: 	W H I L E;
RUN:	R U N;
JOIN:	J O I N;
LOCK:	L O C K;
UNLOCK:	U N L O C K;
RETURN: R E T U R N;

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

COMMENT: (('/*'.*? '*/')|('//' .*? '\n')) -> skip;
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
