package grammar;

import java.util.ArrayList;
import java.util.List;

import grammar.GrammarParser.FuncStatContext;
import grammar.GrammarParser.LockStatContext;
import grammar.GrammarParser.RunStatContext;
import grammar.GrammarParser.TypeContext;
import grammar.GrammarParser.UnlockStatContext;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class PP07PrepWalker extends GrammarBaseListener {

	private Functions functions = new Functions();
	private Locks locks = new Locks();
	private Runs runs = new Runs();
	private List<String> errors = new ArrayList<String>();
	
	public void walk(ParseTree tree) {
		PP07FunctionWalker walker = new PP07FunctionWalker();
		walker.walk(tree);
		functions = walker.getFunctions();
		locks = walker.getLocks();
				
		new ParseTreeWalker().walk(this, tree);
	}
	
	public Functions getFunctions() {
		return functions;
	}
	
	public Locks getLocks() {
		return locks;
	}
	
	public Runs getRuns() {
		return runs;
	}
	
	@Override
	public void exitFuncStat(FuncStatContext ctx) {
		//logic in pp07FunctionWalker
	}
	
	
	@Override
	public void exitRunStat(RunStatContext ctx) {
		if (!runs.addRun(ctx.ID(0).getText(), functions.getFunction(ctx.ID(1).getText()).getReturntype())) {
			addError("Run ID " + ctx.ID(0).getText() + " already declared in program");
		}
	}
	
	public Type getType(TypeContext ctx) {
		if (ctx.getToken(GrammarParser.BOOL, 0) != null) return Type.BOOL;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Type.INT;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Type.VOID;
		return null;
	}
	
	private void addError(String string) {
		errors.add(string);
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
	
}
