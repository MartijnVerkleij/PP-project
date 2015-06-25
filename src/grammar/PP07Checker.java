package grammar;

import grammar.Functions.Function;
import grammar.GrammarParser.*;
import grammar.exception.ParseException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;


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


	public Result check(ParseTree tree) throws ParseException {
		PP07PrepWalker walker = new PP07PrepWalker();
		this.result = new Result();
		this.symbolTable = new SymbolTable();
		this.functions = walker.getFunctions();
		this.locks = walker.getLocks();
		this.runs = walker.getRuns();
		this.errors = walker.getErrors();
		new ParseTreeWalker().walk(this, tree);
		if (hasErrors()) {
			throw new ParseException(getErrors());
		}
		return this.result;
	}

	@Override
	public void exitProgram(ProgramContext ctx) {
		if (ctx.stat().size() < 1) {
			addError("Empty program");
		} else if (!functions.hasFunction("main")) {
			addError("Program contains no main method");
		} else {
			setEntry(ctx, ctx.stat(0));
		}
	}

	@Override
	public void exitDeclStat(DeclStatContext ctx) {
		if (ctx.GLOBAL() == null) {
			if (!symbolTable.add(ctx.ID().getText(), getType(ctx.type())))
				addError("Variable name already declared in local scope");
		} else {
			if (!symbolTable.addGlobal(ctx.ID().getText(), getType(ctx.type())))
				addError("Variable name already declared in global scope");
		}
		if (ctx.expr() != null) {
			if (getType(ctx.type()) != getType(ctx.expr()))
				addError("Assigned type does not equal declared type");
			setEntry(ctx, ctx.expr());
		}
	}

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

	@Override
	public void exitEnumStat(EnumStatContext ctx) {
		if (!symbolTable.addGlobal(ctx.ID().getText(), Type.ENUM))
			addError("Variable name already declared in global scope");
	}


	@Override
	public void exitIfStat(IfStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		setEntry(ctx, ctx.expr());
	}

	@Override
	public void exitWhileStat(WhileStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		setEntry(ctx, ctx.expr());
	}

	@Override
	public void exitBlockStat(BlockStatContext ctx) {
		// fall-through of enterBlock()
		setType(ctx, getType(ctx.block()));
		setEntry(ctx, ctx.block());
	}

	@Override
	public void exitFuncStat(FuncStatContext ctx) {
		// already done in FunctionWalker
		setEntry(ctx, ctx.block());
	}

	@Override
	public void exitExprStat(ExprStatContext ctx) {
		setEntry(ctx, ctx.expr());
	}

	@Override
	public void exitRunStat(RunStatContext ctx) {
		String name = ctx.ID(0).getText();
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
		} else {
			addError("Function " + ctx.ID(0).getText() + " not declared in program");
		}
		setEntry(ctx, function.getContext());
	}

	@Override
	public void exitLockStat(LockStatContext ctx) {
		// left blank
	}

	@Override
	public void exitUnlockStat(UnlockStatContext ctx) {
		if (!locks.releaseLock(ctx.ID().getText()))
			addError("Lock " + ctx.ID().getText() + "never declared in program");
	}

	@Override
	public void exitReturnStat(ReturnStatContext ctx) {
		ParseTree stat = ctx;
		while (!(stat instanceof FuncStatContext)) {
			if (ctx.getParent().getChild(ctx.getParent().getChildCount() - 1) != ctx) {
				addError("Return statement is not last statement.");
			}
			stat = stat.getParent();
		}
		FuncStatContext function = ((FuncStatContext) stat);
		checkType(function.type(0), getType(ctx.expr()));
		setEntry(ctx, ctx.expr());
	}

	@Override
	public void exitBlock(BlockContext ctx) {
		if (!ctx.stat().isEmpty()) {
			setEntry(ctx, ctx.stat(0));
		}
	}

	@Override
	public void exitIntType(IntTypeContext ctx) {
		setType(ctx, Type.INT);
	}

	@Override
	public void exitBoolType(BoolTypeContext ctx) {
		setType(ctx, Type.BOOL);
	}

	@Override
	public void exitVoidType(VoidTypeContext ctx) {
		setType(ctx, Type.VOID);
	}

	@Override
	public void exitFuncCall(FuncCallContext ctx) {
		String name = ctx.ID().getText();
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
		} else {
			addError("Function " + ctx.ID().getText() + " not defined");
		}
		setEntry(ctx, function.getContext());
	}

	@Override
	public void exitJoinExpr(JoinExprContext ctx) {
		if (!runs.hasRun(ctx.ID().getText())) {
			addError("Run statement with ID " + ctx.ID().getText()+ " not declared");
		}
	}
	
	@Override
	public void exitLockedExpr(LockedExprContext ctx) {
		if (!runs.hasRun(ctx.ID().getText())) {
			addError("Run statement with ID " + ctx.ID().getText()+ " not declared");
		}
	}
	
	@Override
	public void exitPlusExpr(PlusExprContext ctx) {
		if (checkType(ctx.expr(0), Type.INT) && checkType(ctx.expr(1), Type.INT)) {
			setType(ctx, Type.INT);
		} else {
			addError("Operation \"" + ctx.plusOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setEntry(ctx, ctx.expr(0));
	}
	
	@Override
	public void exitMultExpr(MultExprContext ctx) {
		if (checkType(ctx.expr(0), Type.INT) && checkType(ctx.expr(1), Type.INT)) {
			setType(ctx, Type.INT);
		} else {
			addError("Operation \"" + ctx.multOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
	}
	
	@Override
	public void exitExpExpr(ExpExprContext ctx) {
		if (checkType(ctx.expr(0), Type.INT) && checkType(ctx.expr(1), Type.INT)) {
			setType(ctx, Type.INT);
		} else {
			addError("Operation \"" + ctx.expOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setEntry(ctx, ctx.expr(0));
	}
	
	@Override
	public void exitBoolExpr(BoolExprContext ctx) {
		if (checkType(ctx.expr(0), Type.BOOL) && checkType(ctx.expr(1), Type.BOOL)) {
			setType(ctx, Type.BOOL);
		} else {
			addError("Operation \"" + ctx.boolOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setEntry(ctx, ctx.expr(0));
	}
	
	@Override
	public void exitCmpExpr(CmpExprContext ctx) {
		if (checkType(ctx.expr(0), Type.BOOL) && checkType(ctx.expr(1), Type.BOOL) 
				&& (ctx.cmpOp().EQ() != null || ctx.cmpOp().NE() != null )) {
			setType(ctx, Type.BOOL);
		} else if (checkType(ctx.expr(0), Type.INT) && checkType(ctx.expr(1), Type.INT)) {
			setType(ctx, Type.INT);
		} else {
			addError("Operation \"" + ctx.cmpOp().getText() + "\" is not defined for operands " 
					+ getType(ctx.expr(0)).toString() + " and " + getType(ctx.expr(1)).toString());
		}
		setEntry(ctx, ctx.expr(0));
	}
	
	@Override
	public void exitPrfExpr(PrfExprContext ctx) {
		if (ctx.prfOp().NOT() != null && checkType(ctx, Type.BOOL)) {
			setType(ctx, Type.BOOL);
		} else if (ctx.prfOp().MINUS() != null && checkType(ctx, Type.INT)) {
			setType(ctx, Type.INT);
		}
		setEntry(ctx, ctx.expr());
	}
	
	@Override
	public void exitParExpr(ParExprContext ctx) {
		setType(ctx, getType(ctx.expr()));
		setEntry(ctx, ctx.expr());
	}
	
	@Override
	public void exitIdExpr(IdExprContext ctx) {
		setType(ctx, getType(ctx.ID()));
		setEntry(ctx, ctx);
	}
	
	@Override
	public void exitNumExpr(NumExprContext ctx) {
		setType(ctx, Type.INT);
		setEntry(ctx, ctx);
	}
	
	@Override
	public void exitEidExpr(EidExprContext ctx) {
		setType(ctx, Type.INT);
		setEntry(ctx, ctx);
	}
	
	@Override
	public void exitTrueExpr(TrueExprContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}
	
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
	private boolean checkType(ParserRuleContext node, Type expected) {
		Type actual = getType(node);
		if (actual == null) {
			throw new IllegalArgumentException("Missing inferred type of "
					+ node.getText());
		}
		if (!actual.equals(expected)) {
			addError(node.getText() + "\nExpected type '" + expected + "' but found '" + actual + "'");
			return false;
		}
		return true;
	}

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
