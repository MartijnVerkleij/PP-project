grammar Grammar;

@header{package grammar;}

// Program consists of one or more statements
program	: stat+;

/* Statements
* 	declStat	- Declaration and Optional Assignment
*		Declare statement, declare a (optionally global) variable and optionally assign an expression to it
*	assStat		- Assignment
*		Assign statement, assign an expression to a variable
*	enumStat	- Enumeration Declaration
*		Enumeration statement, declare an (optionally global) enumeration, and assign values to it. Assignment must
*		occur in this phase
*	ifStat		- If
*		If statement, with expression between parentheses followed by a statement and optionally 'else' and another
*		statement
*	whileStat	- While
*		While statement, with expression between parentheses followed by a statement
*	blockStat	- Block
*		Block statement, see 'block'
*	funcStat	- Function Declaration
*		Function declaration, consisting of a type, id, parameters and a block statement
*	exprStat	- Expression
*		Expression statement, an expression can be a statement, e.g. a function call
*	runStat		- Run
*		Run statement, which creates a new thread that executes a function defined in the call to run, parameters to
*		this function are also passed along at this stage as expressions
*	lockStat	- Lock
*		Looks up a lock in memory, if it's zero, set it to one and continue, if it's one, try again. This effectively
*		"locks" the lock
*	unlockStat	- Unlock
*		Set a lock in memory to zero, effectively "unlocking" the lock
*	returnStat	- Return
*		Return statement, sets return value of the function. Must be located at top-level of the function scope
*/
stat	: GLOBAL? type ID (ASS expr)? SEMI							#declStat
		| ID ASS expr SEMI 											#assStat
		| ENUM ID ASS LBRACE EID (COMMA EID)* RBRACE SEMI			#enumStat
		| IF LPAR expr RPAR stat (ELSE stat)?						#ifStat
		| WHILE LPAR expr RPAR stat									#whileStat
		| block														#blockStat
		| type ID LPAR (type ID (COMMA type ID)*)? RPAR block		#funcStat
		| expr SEMI													#exprStat
		| RUN ID LPAR (ID (COMMA expr)*) RPAR SEMI					#runStat
		| LOCK ID SEMI												#lockStat
		| UNLOCK ID SEMI											#unlockStat
		| RETURN expr SEMI											#returnStat
		;

/*	Block Statement
*		Contains zero or more statements between two braces
*/
block 	: LBRACE stat* RBRACE
		;

/* TypeSize
*	INT
*		Integer type
*	BOOL
*		Boolean type
*	VOID
*		Empty type
*/
type	: INT														#intType
		| BOOL														#boolType
		| VOID														#voidType
		;

/* Expressions
*	funcCall 	- Function Call Expression
*		Call a function
*	joinExpr	- Join Expression
*		After new a thread is created with 'run', it's return value can be obtained using 'join'. Note that this will
*		block the current thread if there is either no return value (call to void function) or the thread has not not
*		finished its execution yet. The former will block indefinately, the latter will block until the return value of
*		the thread is set
*	lockedExpr	- Locked Expression
*		Checks if a lock is 'locked', returns a boolean. See 'lock' and 'unlock'.
*	plusExpr	- Addition Expression
*		Addition and subtraction
*	multExpr	- Multiplication Expression
*		Multiplication and integer division
*	expExpr		- Exponent Expression
*		Exponentiation b^n. Where b is any integer and n is any non-negative integer
*	boolExpr	- Boolean Expression
*		And and or operations
*	cmpExpr		- Comparison Expression
*		Comparison operations
*	prfExpr		- Prefix Expression
*		Negate and not operations
*	parExpr		- Parentheses Expression
*		Parentheses
*	idExpr		- ID Expression
*		An ID
*	numExpr		- Number Expression
*		A number
*	eidExpr		- Enumeration ID Expression
*		An enum ID
*	trueExpr	- True Expression
*		True
*	falseExpr	- False Expression
*		False
*/
expr	: ID LPAR (expr (COMMA expr)*)? RPAR						#funcCall
		| JOIN ID													#joinExpr
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

// Operators
plusOp	: PLUS | MINUS; 				// + and -
multOp	: STAR | SLASH;					// * and /
expOp	: CARET;						// ^
boolOp	: AND | OR;						// AND and OR
cmpOp	: EQ | GT | GE | LT | LE | NE;	// ==, >, >=, <, <= and !=
prfOp	: MINUS | NOT;					// - and !

// Keywords
INT: 	I N T;
BOOL: 	B O O L;
VOID:	V O I D;
ENUM: 	E N U M;

GLOBAL: G L O B A L;
IF: 	I F;
ELSE: 	E L S E;
WHILE: 	W H I L E;
RUN:	R U N;
JOIN:	J O I N;
LOCK:	L O C K;
UNLOCK:	U N L O C K;
LOCKED:	L O C K E D;
RETURN: R E T U R N;
TRUE:	T R U E;
FALSE:	F A L S E;

// Symbols
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
UND:	'_';

PLUS:   '+';
MINUS:  '-';
SLASH:  '/';
STAR:   '*';
CARET:	'^';

/* IDs, numbers and strings
*	ID
*		Starts with a lowercase letter, followed by zero or more letters, underscores and digits
*	EID
*		Starts with an uppercase letter, followed by zero or more letters, underscores and digits
*	NUM
*		Consists of one or more digits
*	STR
*		Consists of any characters between two double quotes
*/
ID: 	LCASEL (LETTER | UND | DIGIT)*;
EID:	UCASEL (UCASEL | UND | DIGIT)*;
NUM:	DIGIT+;
STR: 	DQUOTE .*? DQUOTE;

// Fragments
fragment LETTER: [a-zA-Z]; 	// Any letter
fragment UCASEL: [A-Z];		// Any uppercase letter
fragment LCASEL: [a-z];		// Any lowercase letter
fragment DIGIT: [0-9];		// Any digit

// Below are all letters in either upper or lower case, these are used to define keywords.
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

// Skip comments (line and block) and whitespace
COMMENT: (('/*'.*? '*/')|('//' .*? '\n')) -> skip;
WS: [ \t\r\n]+ -> skip;
