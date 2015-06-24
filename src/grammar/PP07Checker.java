package grammar;

import java.util.ArrayList;
import java.util.List;

import grammar.GrammarParser.AssStatContext;
import grammar.GrammarParser.BlockStatContext;
import grammar.GrammarParser.DeclStatContext;
import grammar.GrammarParser.EnumStatContext;
import grammar.GrammarParser.FuncStatContext;
import grammar.GrammarParser.IfStatContext;
import grammar.GrammarParser.ProgramContext;
import grammar.GrammarParser.WhileStatContext;
import grammar.Type.Types;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class PP07Checker extends GrammarBaseListener {
	
	private ParseTree checkedTree;
	private List<String> errors = new ArrayList<String>();
	private List<String> functions = new ArrayList<String>();
	private ParseTreeProperty<Type.Types> nodeType = new ParseTreeProperty<Type.Types>();
	private SymbolTable symbolTable = new SymbolTable();
	
	public ParseTree check(ParseTree tree) {
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
		if (!symbolTable.addGlobal(ctx.ID().getText(), Types.ENUM))
			addError("Variable name already declared in global scope");
	}
	
	
	@Override
	public void enterIfStat(IfStatContext ctx) {
		if (nodeType.get(ctx.expr()) != Types.BOOL) {
			addError("If statement requires a boolean expression");
		}
	}
	
	@Override
	public void enterWhileStat(WhileStatContext ctx) {
		if (nodeType.get(ctx.expr()) != Types.BOOL) {
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
		
	}
	

	@Override
	public void exitProgram(ProgramContext ctx) {
		if (!functions.contains("main")) {
			addError("Program type no main method");
		}
	}

	private void addError(String string) {
		errors.add(string);
	}
}
