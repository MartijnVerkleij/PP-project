package grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import grammar.GrammarParser.DeclStatContext;
import grammar.GrammarParser.ProgramContext;
import grammar.GrammarParser.StatContext;
import grammar.Type.Types;
import grammar.exception.ParseException;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class PP07Checker extends GrammarBaseListener {
	
	private ParseTree checkedTree;
	private List<String> errors = new ArrayList<String>();
	private List<String> functions = new ArrayList<String>();
	private ParseTreeProperty<Type.Types> nodetype = new ParseTreeProperty<Type.Types>();
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
	public void exitProgram(ProgramContext ctx) {
		if (!functions.contains("main")) {
			addError("Program contains no main method");
		}
	}

	private void addError(String string) {
		errors.add(string);
	}
}
