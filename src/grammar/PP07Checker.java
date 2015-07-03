package grammar;

import grammar.Functions.Function;
import grammar.GrammarParser.*;
import grammar.exception.ParseException;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

/**
 * Checks a given PP07 parse Tree on its validity as PP07 code. It uses a 
 * ParseTreeWalker to walk through the tree, checking code on typing, decla-
 * ration and generates a control flow graph and types of expressions.
 * @author tim, martijn
 */

public class PP07Checker extends GrammarBaseListener {

	/**
	 * Result of the latest call of {@link #check}.
	 */
	private Result result;

	/**
	 * Variable scopes for the latest call of {@link #check}.
	 */
	private SymbolTable symbolTable;

	/**
	 * List of errors collected in the latest call of {@link #check}.
	 */
	private List<String> errors;

	/**
	 * List of functions collected in the latest call of {@link #check}.
	 */
	private Functions functions;

	/**
	 * List of locks collected in the latest call of {@link #check}.
	 */
	private Locks locks;

	/**
	 * List of run declarations collected in the latest call of {@link #check}.
	 */
	private Runs runs;

	/**
	 * Checks a given PP07 parse Tree on its validity as PP07 code. It uses a 
	 * ParseTreeWalker to walk through the tree, checking code on typing, decla-
	 * ration and generates a control flow graph and types of expressions.
	 * @param tree ParseTree of PP07 source code
	 * @return List of 
	 * @throws ParseException on errors in the source code that prevent code 
	 * generation
	 */
	public Result check(ParseTree tree) throws ParseException {
		PP07PrepWalker walker = new PP07PrepWalker();
		walker.walk(tree);
		this.result = new Result();
		this.symbolTable = new SymbolTable();
		this.functions = walker.getFunctions();
		this.locks = walker.getLocks();
		this.runs = walker.getRuns();
		this.errors = walker.getErrors();
		new ParseTreeWalker().walk(this, tree);
		if (hasErrors()) {
			errors.forEach(System.err::println);
			throw new ParseException(getErrors());
		}
		return this.result;
	}

	/**
	 * Checks for an empty program, and has code for detecting the absence 
	 * of a main method. Thst code is not used because of missing Function
	 * code in the generator.
	 */
	@Override
	public void exitProgram(ProgramContext ctx) {
		if (ctx.stat().size() < 1) {
			addError("Empty program");
		} else if (!functions.hasFunction("main")) {
//			addError("Program contains no main method");
		} else {
			setEntry(ctx, ctx.stat(0));
		}
	}

	/**
	 * Check if variable name does not collide. expression is checked for 
	 * being the correct type.
	 */
	@Override
	public void exitDeclStat(DeclStatContext ctx) {
		if (ctx.GLOBAL() == null) {
			if (!symbolTable.add(ctx.ID().getText(), getType(ctx.type())))
				addError("Variable name " + ctx.ID().getText() + " already declared in local scope");
		} else {
			if (!symbolTable.addGlobal(ctx.ID().getText(), getType(ctx.type())))
				addError("Variable name " + ctx.ID().getText() + " already declared in global scope");
		}
		if (ctx.expr() != null) {
			if (getType(ctx.type()) != getType(ctx.expr()))
				addError("Assigned type " + getType(ctx.expr()).name() 
						+ " does not equal declared type " + getType(ctx.type()).name());
			setEntry(ctx, ctx.expr());
		} else {
			setEntry(ctx, ctx);
		}
	}

	/**
	 * Assignment is checked at being of the correct type. Also checks if the
	 * variable was declared at all.
	 */
	@Override
	public void exitAssStat(AssStatContext ctx) {
		if (symbolTable.type(ctx.ID().getText()) == null)
			addError("\"" + ctx.ID().getText() + "\" was not declared in any scope");
		if (symbolTable.type(ctx.ID().getText()) != getType(ctx.expr())) {
			addError("Assignment is of wrong type. Expected: " +
					symbolTable.type(ctx.ID().getText()) +
					" Actual: " + getType(ctx.expr()));
		}
		setEntry(ctx, ctx.expr());
	}

	/**
	 * incomplete enum declaration. Not used in code generation
	 */
	@Override
	public void exitEnumStat(EnumStatContext ctx) {
		if (!symbolTable.addGlobal(ctx.ID().getText(), Type.ENUM))
			addError("Variable name already declared in global scope");
		setEntry(ctx, ctx);
	}

	/**
	 * Checks whether the type of the expression is boolean.
	 */
	@Override
	public void exitIfStat(IfStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		setEntry(ctx, ctx.expr());
	}

	/**
	 * Checks whether the type of the expression is boolean
	 */
	@Override
	public void exitWhileStat(WhileStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		setEntry(ctx, ctx.expr());
	}

	/**
	 * Fall-through of enterBlock. passes on information.
	 */
	@Override
	public void exitBlockStat(BlockStatContext ctx) {
		// fall-through of enterBlock()
		setEntry(ctx, entry(ctx.block()));
	}
	
	/**
	 * Opens a new scope for the function, containing the arguments.
	 */
	@Override
	public void enterFuncStat(FuncStatContext ctx) {
		symbolTable.openScope();
		for (int i = 1; i < ctx.ID().size(); i++) {
			if (!symbolTable.addGlobal(ctx.ID(i).getText(), getType(ctx.type(i))))
				addError("Variable name " + ctx.ID(i).getText() + " already declared in global scope");
		}
	}
	
	/**
	 * Closes the function scope, which contained the arguments. Also
	 * sets the context of the Function object associated with this
	 * function.
	 * See FunctionWalker.exitFuncStat() for more code on this matter.
	 */
	@Override
	public void exitFuncStat(FuncStatContext ctx) {
		symbolTable.closeScope();
		// already done in FunctionWalker
		setEntry(ctx, entry(ctx.block()));
		functions.getFunction(ctx.ID(0).getText()).setContext(ctx);
	}

	/**
	 * Sets the CFG enty.
	 */
	@Override
	public void exitExprStat(ExprStatContext ctx) {
		setEntry(ctx, entry(ctx.expr()));
	}

	/**
	 * Checks if the Function that the run points to exists, has the right
	 * arguments. 
	 */
	@Override
	public void exitRunStat(RunStatContext ctx) {
		String name = ctx.ID(1).getText();
		Function function = functions.getFunction(name);
		if (functions.hasFunction(name)) {
			if (ctx.expr().size() == function
					.getArgumentCount()) {
				for (int i = 0; i < ctx.expr().size(); i++) {
					if (!getType(ctx.expr(i)).equals(function.getArgument(i))) {
						addError("Argument " + i + " of run call " + name
								+ " did not match expected type. "
								+ "Expected: " + function.getArgument(i)
								+ " Actual: " + getType(ctx.expr(i)));
					}
				}
			} else {
				addError("Argument count of call " + name + " did not match. "
						+ "Expected: " + function.getArgumentCount()
						+ " Actual: " + ctx.expr().size());
			}
			setEntry(ctx, entry(function.getContext()));
		} else {
			addError("Function " + ctx.ID(0).getText() + " not declared in program");
		}

	}

	/**
	 * Sets the CFG entry for the given node.
	 */
	@Override
	public void exitLockStat(LockStatContext ctx) {
		setEntry(ctx, ctx);
	}

	/**
	 * Checks whether the lock is declared.
	 */
	@Override
	public void exitUnlockStat(UnlockStatContext ctx) {
		if (!locks.releaseLock(ctx.ID().getText()))
			addError("Lock " + ctx.ID().getText() + "never declared in program");
		setEntry(ctx, ctx);
	}

	/**
	 * Checks whether this return statement is the last 
	 * statement in the block.
	 */
	@Override
	public void exitReturnStat(ReturnStatContext ctx) {
		ParseTree stat = ctx;
		while (!(stat instanceof FuncStatContext)) {
			if (ctx.getParent().getChild(ctx.getParent().getChildCount() - 2) != ctx) {
				addError("Return statement is not last statement.");
			}
			stat = stat.getParent();
		}
		FuncStatContext function = ((FuncStatContext) stat);
		checkType(function.type(0), getType(ctx.expr()));
		setEntry(ctx, ctx.expr());
	}

	/**
	 * Opens the scope for the given block.
	 */
	@Override
	public void enterBlock(@NotNull BlockContext ctx) {
		symbolTable.openScope();
	}

	/**
	 * Closes the scope for the given block.
	 */
	@Override
	public void exitBlock(BlockContext ctx) {
		if (!ctx.stat().isEmpty()) {
			setEntry(ctx, entry(ctx.stat(0)));
		}
		symbolTable.closeScope();
	}

	/**
	 * Setting type to INT
	 */
	@Override
	public void exitIntType(IntTypeContext ctx) {
		setType(ctx, Type.INT);
	}

	/**
	 * Setting type to BOOL
	 */
	@Override
	public void exitBoolType(BoolTypeContext ctx) {
		setType(ctx, Type.BOOL);
	}

	/**
	 * Setting type to VOID
	 */
	@Override
	public void exitVoidType(VoidTypeContext ctx) {
		setType(ctx, Type.VOID);
	}

	/**
	 * Checks whether this function call matches arguments with the
	 * function it calls. Sets the type of this expression to the
	 * return type of the function.
	 */
	@Override
	public void exitFuncCall(FuncCallContext ctx) {
		String name = ctx.ID().getText();
		Function function = functions.getFunction(name);
		if (functions.hasFunction(name)) {
			if (ctx.expr().size() == function
					.getArgumentCount()) {
				boolean good = true;
				for (int i = 0; i < ctx.expr().size(); i++) {
					if (!getType(ctx.expr(i)).equals(function.getArgument(i))) {
						addError("Argument " + i + " of run call " + name
								+ " did not match expected type. "
								+ "Expected: " + function.getArgument(i)
								+ " Actual: " + getType(ctx.expr(i)));
						good = false;
					}
					if (good) {
						setType(ctx, function.getReturntype());
					}
				}
			} else {
				addError("Argument count of call " + name + " did not match. "
						+ "Expected: " + function.getArgumentCount()
						+ " Actual: " + ctx.expr().size());
			}
			setEntry(ctx, entry(function.getContext()));
		} else {
			addError("Function " + ctx.ID().getText() + " not defined");
		}
	}

	/**
	 * Gets the return type of the run's function to set this 
	 * expression to. If no run exists with that name, it 
	 * gives an error.
	 */
	@Override
	public void exitJoinExpr(JoinExprContext ctx) {
		if (!runs.hasRun(ctx.ID().getText())) {
			addError("Run statement with ID " + ctx.ID().getText()+ " not declared");
		}
		setType(ctx, runs.getType(ctx.ID().getText()));
		setEntry(ctx, ctx);
	}
	
	/**
	 * Checks the existence of a lock with the given name. Sets the
	 * type of the expression to BOOL.
	 */
	@Override
	public void exitLockedExpr(LockedExprContext ctx) {
		if (!locks.releaseLock(ctx.ID().getText())) {
			addError("Run statement with ID " + ctx.ID().getText()+ " not declared");
		}
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}
	
	/**
	 * Checks input types, INT and INT, and sets return type to INT
	 */
	@Override
	public void exitPlusExpr(PlusExprContext ctx) {
		if (!compareType(ctx.expr(0), Type.INT) && compareType(ctx.expr(1), Type.INT)) {
			addError("Operation \"" + ctx.plusOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setType(ctx, Type.INT);
		setEntry(ctx, entry(ctx.expr(0)));
	}
	
	/**
	 * Checks input types, INT and INT, and sets return type to INT
	 */
	@Override
	public void exitMultExpr(MultExprContext ctx) {
		if (compareType(ctx.expr(0), Type.INT) && compareType(ctx.expr(1), Type.INT)) {
			addError("Operation \"" + ctx.multOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setType(ctx, Type.INT);
		setEntry(ctx, entry(ctx.expr(0)));
	}
	
	/**
	 * Checks input types, INT and INT, and sets return type to INT
	 */
	@Override
	public void exitExpExpr(ExpExprContext ctx) {
		if (compareType(ctx.expr(0), Type.INT) && compareType(ctx.expr(1), Type.INT)) {
			setType(ctx, Type.INT);
		} else {
			addError("Operation \"" + ctx.expOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setEntry(ctx, entry(ctx.expr(0)));
	}
	
	/**
	 * Checks input types, BOOL and BOOL, and sets return type to BOOL
	 */
	@Override
	public void exitBoolExpr(BoolExprContext ctx) {
		if (compareType(ctx.expr(0), Type.BOOL) && compareType(ctx.expr(1), Type.BOOL)) {
			setType(ctx, Type.BOOL);
		} else {
			addError("Operation \"" + ctx.boolOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setEntry(ctx, entry(ctx.expr(0)));
	}
	
	/**
	 * Depending on the operation (!= or ==), accepts two BOOLs or two 
	 * INTs, and returns a BOOL.
	 */
	@Override
	public void exitCmpExpr(CmpExprContext ctx) {
		if (compareType(ctx.expr(0), Type.BOOL) && compareType(ctx.expr(1), Type.BOOL) 
				&& (ctx.cmpOp().EQ() != null || ctx.cmpOp().NE() != null )) {
			setType(ctx, Type.BOOL);
		} else if (compareType(ctx.expr(0), Type.INT) && compareType(ctx.expr(1), Type.INT)) {
			setType(ctx, Type.BOOL);
		} else {
			addError("Operation \"" + ctx.cmpOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setEntry(ctx, entry(ctx.expr(0)));
	}
	
	/**
	 * Depending on the operation (! or -) accepts a BOOL or INT, and 
	 * returns a BOOL or INT respectively
	 */
	@Override
	public void exitPrfExpr(PrfExprContext ctx) {
		if (ctx.prfOp().NOT() != null && compareType(ctx.expr(), Type.BOOL)) {
			setType(ctx, Type.BOOL);
		} else if (ctx.prfOp().MINUS() != null && compareType(ctx.expr(), Type.INT)) {
			setType(ctx, Type.INT);
		}
		setEntry(ctx, entry(ctx.expr()));
	}
	
	/**
	 * Type of expression is passed on.
	 */
	@Override
	public void exitParExpr(ParExprContext ctx) {
		setType(ctx, getType(ctx.expr()));
		setEntry(ctx, entry(ctx.expr()));
	}
	
	/**
	 * Gets the Type of the variable and sets the type of this expression
	 * to it. Gives an error if the variable was not declared.
	 */
	@Override
	public void exitIdExpr(IdExprContext ctx) {
		String id = ctx.ID().getText();
		Type type = symbolTable.type(id);
		if (type == null) {
			addError("ID: " + id + " is not defined.");
		} else {
			setType(ctx, type);
			setEntry(ctx, ctx);
		}
	}
	
	/**
	 * Sets the expression type to INT.
	 */
	@Override
	public void exitNumExpr(NumExprContext ctx) {
		setType(ctx, Type.INT);
		setEntry(ctx, ctx);
	}
	
	/**
	 * Sets the expression type to INT.
	 */
	@Override
	public void exitEidExpr(EidExprContext ctx) {
		setType(ctx, Type.INT);
		setEntry(ctx, ctx);
	}
	
	/**
	 * Sets the expression type to BOOL.
	 */
	@Override
	public void exitTrueExpr(TrueExprContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}
	
	/**
	 * Sets the expression type to BOOL.
	 */
	@Override
	public void exitFalseExpr(FalseExprContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}
	
	/**
	 * Indicates if any errors were encountered in this tree listener.
	 */
	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}

	/**
	 * Returns the list of errors collected in this tree listener.
	 */
	public List<String> getErrors() {
		return this.errors;
	}

	/**
	 * Checks the inferred type of a given parse tree,
	 * and adds an error if it does not correspond to the expected type.
	 */
	private void checkType(ParserRuleContext node, Type expected) {
		Type actual = getType(node);
		if (actual == null) {
			throw new IllegalArgumentException("Missing inferred type of "
					+ node.getText());
		}
		if (!actual.equals(expected)) {
			addError(node.getText() + "\nExpected type '" + expected + "' but found '" + actual + "'");
			return;
		}
		return;
	}
	
	/**
	 * Checks the inferred type of a given parse tree,
	 * and returns whether it is correct. It works just
	 * like <code>checkType()</code>, except for that 
	 * it does not throw an error.
	 */
	private boolean compareType(ParserRuleContext node, Type expected) {
		Type actual = getType(node);
		if (actual == null) {
			throw new IllegalArgumentException("Missing inferred type of "
					+ node.getText());
		}
		if (!actual.equals(expected)) {
			return false;
		}
		return true;
	}

	/**
	 * Adds an error to the list of errors.
	 * @param string error message
	 */

	private void addError(String string) {
		errors.add(string);
	}

	/**
	 * Convenience method to add an offset to the result.
	 */
	private void setOffset(ParseTree node, Integer offset) {
		this.result.setOffset(node, offset);
	}

	/**
	 * Convenience method to add a type to the result.
	 */
	private void setType(ParseTree node, Type type) {
		this.result.setType(node, type);
	}

	/**
	 * Returns the type of a given expression or type node.
	 */
	private Type getType(ParseTree node) {
		return this.result.getType(node);
	}
	
	/**
	 * Gets the type of type nodes. These do not have an enter- or exit method,
	 * so they are defined here.
	 * @param ctx context of the ttype node we want to know the Type of.
	 * @return Type that was found, or null if none was found.
	 */
	public Type getType(TypeContext ctx) {
		if (ctx.getToken(GrammarParser.BOOL, 0) != null) return Type.BOOL;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Type.INT;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Type.VOID;
		return null;
	}

	/**
	 * Convenience method to add a flow graph entry to the result.
	 */
	private void setEntry(ParseTree node, ParserRuleContext entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Null flow graph entry");
		}
		this.result.setEntry(node, entry);
	}

	/**
	 * Returns the flow graph entry of a given expression or statement.
	 */
	private ParserRuleContext entry(ParseTree node) {
		return this.result.getEntry(node);
	}
}
