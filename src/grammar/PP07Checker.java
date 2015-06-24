package grammar;

import java.util.ArrayList;
import java.util.List;

import grammar.Functions.Function;
import grammar.GrammarParser.AssStatContext;
import grammar.GrammarParser.BlockStatContext;
import grammar.GrammarParser.DeclStatContext;
import grammar.GrammarParser.EnumStatContext;
import grammar.GrammarParser.ExprStatContext;
import grammar.GrammarParser.FuncStatContext;
import grammar.GrammarParser.IfStatContext;
import grammar.GrammarParser.LockStatContext;
import grammar.GrammarParser.LockedExprContext;
import grammar.GrammarParser.ProgramContext;
import grammar.GrammarParser.RunStatContext;
import grammar.GrammarParser.StatContext;
import grammar.GrammarParser.TypeContext;
import grammar.GrammarParser.WhileStatContext;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


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


	public ParseTree check(ParseTree tree) {
		this.result = new Result();
		this.symbolTable = new SymbolTable();
		this.errors = new ArrayList<>();
		this.functions = new PP07FunctionWalker().walkFunctions(tree);
		new ParseTreeWalker().walk(this, tree);
		if (errors.isEmpty()) {
			return tree;
		} else {
			return null;
		}
	}
	
	@Override
	public void enterProgram(ProgramContext ctx) {
		if (ctx.stat().size() < 1) {
			addError("Empty program");
		}
	}
	
	@Override
	public void enterDeclStat(DeclStatContext ctx) {
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
		}	
	}
	
	@Override
	public void enterAssStat(AssStatContext ctx) {
		if (symbolTable.type(ctx.ID().getText()) == null)
			addError("\"" + ctx.ID().getText() + "\" was not declared in any scope");
		if (symbolTable.type(ctx.ID().getText()) != getType(ctx.expr())) {
			addError("Assignment is of wrong type. Expected: " + 
					symbolTable.type(ctx.ID().getText()) +
					" Actual: " + getType(ctx.expr()));
		}
	}
	
	@Override
	public void enterEnumStat(EnumStatContext ctx) {
		if (!symbolTable.addGlobal(ctx.ID().getText(), Type.ENUM))
			addError("Variable name already declared in global scope");
	}
	
	
	@Override
	public void enterIfStat(IfStatContext ctx) {
		if (getType(ctx.expr()) != Type.BOOL) {
			addError("If statement requires a boolean expression");
		}
	}
	
	@Override
	public void enterWhileStat(WhileStatContext ctx) {
		if (getType(ctx.expr()) != Type.BOOL) {
			addError("While statement requires a boolean expression");
		}
	}
	
	@Override
	public void enterBlockStat(BlockStatContext ctx) {
		// fall-through of enterBlock()
		setType(ctx, getType(ctx.block()));
	}
	
	@Override
	public void enterFuncStat(FuncStatContext ctx) {
		// already done in FunctionWalker
		
	}
	
	@Override
	public void enterExprStat(ExprStatContext ctx) {
		// left blank
	}
	
	@Override
	public void enterRunStat(RunStatContext ctx) {
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
		
		
	}
	
	@Override
	public void enterLockStat(LockStatContext ctx) {
		
	}
	
	
	

	@Override
	public void exitProgram(ProgramContext ctx) {
		if (!functions.hasFunction("main")) {
			addError("Program contains no main method");
		}
	}

	private void addError(String string) {
		errors.add(string);
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
}
