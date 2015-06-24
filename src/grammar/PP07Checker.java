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
import grammar.GrammarParser.ProgramContext;
import grammar.GrammarParser.RunStatContext;
import grammar.GrammarParser.StatContext;
import grammar.GrammarParser.TypeContext;
import grammar.GrammarParser.WhileStatContext;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class PP07Checker extends GrammarBaseListener {
	
	private ParseTree checkedTree;
	private List<String> errors = new ArrayList<String>();
	private Functions functions;
	private ParseTreeProperty<Type> nodeType = new ParseTreeProperty<Type>();

	private SymbolTable symbolTable = new SymbolTable();
	
	public ParseTree check(ParseTree tree) {
		functions = new PP07FunctionWalker().walkFunctions(tree);
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
		boolean error = false;
		if (ctx.GLOBAL() == null) {
			if (!symbolTable.add(ctx.ID().getText(), nodeType.get(ctx.type())))
				addError("Variable name already declared in local scope");
		} else {
			if (!symbolTable.addGlobal(ctx.ID().getText(), nodeType.get(ctx.type())))
				addError("Variable name already declared in global scope");;
		}
		if (ctx.expr() != null) {
			if (nodeType.get(ctx.type()) != nodeType.get(ctx.expr()))
				addError("Assigned type does not equal declared type");
		}	
	}
	
	@Override
	public void enterAssStat(AssStatContext ctx) {
		if (symbolTable.type(ctx.ID().getText()) == null)
			addError("\"" + ctx.ID().getText() + "\" was not declared in any scope");
		if (symbolTable.type(ctx.ID().getText()) != nodeType.get(ctx.expr())) {
			addError("Assignment is of wrong type. Expected: " + 
					symbolTable.type(ctx.ID().getText()) +
					" Actual: " + nodeType.get(ctx.expr()));
		}
	}
	
	@Override
	public void enterEnumStat(EnumStatContext ctx) {
		if (!symbolTable.addGlobal(ctx.ID().getText(), Type.ENUM))
			addError("Variable name already declared in global scope");
	}
	
	
	@Override
	public void enterIfStat(IfStatContext ctx) {
		if (nodeType.get(ctx.expr()) != Type.BOOL) {
			addError("If statement requires a boolean expression");
		}
	}
	
	@Override
	public void enterWhileStat(WhileStatContext ctx) {
		if (nodeType.get(ctx.expr()) != Type.BOOL) {
			addError("While statement requires a boolean expression");
		}
	}
	
	@Override
	public void enterBlockStat(BlockStatContext ctx) {
		// fall-through of enterBlock()
		nodeType.put(ctx, nodeType.get(ctx.block()));
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
					if (!nodeType.get(ctx.expr(i)).equals(function.getArgument(i))) {
						addError("Argument " + i + " of run call " + name 
								+ " did not match expected type. "
								+ "Expected: " + function.getArgument(i) 
								+ " Actual: " + nodeType.get(ctx.expr(i)));
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
	public void exitProgram(ProgramContext ctx) {
		if (!functions.hasFunction("main")) {
			addError("Program contains no main method");
		}
	}
	public Type getType(TypeContext ctx) {
		if (ctx.getToken(GrammarParser.BOOL, 0) != null) return Type.BOOL;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Type.INT;
//		if (ctx.getToken(GrammarParser.INT, 0) != null) return TypeSize.VOID;
		return null;
	}

	private void addError(String string) {
		errors.add(string);
	}
}
