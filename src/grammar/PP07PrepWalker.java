package grammar;

import java.util.ArrayList;
import java.util.List;

import grammar.Functions.Function;
import grammar.GrammarParser.FuncStatContext;
import grammar.GrammarParser.LockStatContext;
import grammar.GrammarParser.RunStatContext;
import grammar.GrammarParser.TypeContext;
import grammar.GrammarParser.UnlockStatContext;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Walker that walks the second iteration of the tree. 
 * It is required for proper run declaration checking.
 * @author tim, martijn
 *
 */
public class PP07PrepWalker extends GrammarBaseListener {

	private Functions functions = new Functions();
	private Locks locks = new Locks();
	private Runs runs = new Runs();
	private List<String> errors = new ArrayList<String>();
	
	/**
	 * Walk the whole tree to look for run calls. Passes on 
	 * functions and locks to the PP07Checker.
	 * @param tree
	 */
	public void walk(ParseTree tree) {
		PP07FunctionWalker walker = new PP07FunctionWalker();
		walker.walk(tree);
		functions = walker.getFunctions();
		locks = walker.getLocks();
				
		new ParseTreeWalker().walk(this, tree);
	}
	
	/**
	 * Getter for Functions
	 * @return
	 */
	public Functions getFunctions() {
		return functions;
	}
	
	/**
	 * Getter for Locks
	 * @return
	 */
	public Locks getLocks() {
		return locks;
	}
	
	/**
	 * Getter for runs
	 * @return
	 */
	public Runs getRuns() {
		return runs;
	}
	
	/**
	 * Checks run statements for argument count and adds them to the Runs list.
	 */
	@Override
	public void exitRunStat(RunStatContext ctx) {
		Function function = functions.getFunction(ctx.ID(1).getText());
		if (function != null) {
			if (!runs.addRun(ctx.ID(0).getText(), function.getReturntype())) {
				addError("Run ID " + ctx.ID(0).getText() + " already declared in program");
			}
		} else {
			addError("Function " + ctx.ID(1).getText() + " not declared in program");
		}
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
		if (ctx.getToken(GrammarParser.VOID, 0) != null) return Type.VOID;
		return null;
	}
	
	/**
	 * Adds an error to the list of errors.
	 * @param string
	 */
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
