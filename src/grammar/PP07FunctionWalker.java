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

/**
 * Walker that walks the tree in search of Function statements
 * and Lock statements, and adds them to their respective lists
 * and passes them on to the PP07PrepWalker.
 * @author tim, martijn
 *
 */
public class PP07FunctionWalker extends GrammarBaseListener {

	/**
	 * list of all Functions found
	 */
	private Functions functions = new Functions();
	
	/**
	 * List of all locks found
	 */
	private Locks locks = new Locks();
	
	/**
	 * List of errors found.
	 */
	private List<String> errors = new ArrayList<String>();
	
	/**
	 * Walks the tree in order.
	 * @param tree
	 */
	public void walk(ParseTree tree) {
		new ParseTreeWalker().walk(this, tree);
	}
	
	/**
	 * Getter for Functions
	 * @return Functions object containing all found Functions.
	 */
	public Functions getFunctions() {
		return functions;
	}
	
	/**
	 * Getter for Locks
	 * @return Locks object containing all locks found.
	 */
	public Locks getLocks() {
		return locks;
	}
	
	/**
	 * Adds the function to the list of functions, passing on the
	 * types declared.
	 */
	@Override
	public void exitFuncStat(FuncStatContext ctx) {
		Type[] arguments = new Type[ctx.ID().size() - 1];
		for (int i = 0; i < ctx.ID().size() - 1; i++) {
			arguments[i] = getType(ctx.type( i + 1));
		}
		if (!functions.addFunction(ctx.ID(0).getText(), getType(ctx.type(0)) , arguments)) {
			addError("Function name " + ctx.ID(0).getText() + " already declared in program");
		}
	}
	
	/**
	 * Makes a new lock object.
	 */
	@Override
	public void exitLockStat(LockStatContext ctx) {
		locks.acquireLock(ctx.ID().getText());
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
