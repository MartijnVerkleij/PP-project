package grammar;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class PP07Checker extends GrammarBaseListener {
	
	private ParseTree checkedTree;
	private int errors = 0;
	
	public ParseTree check(ParseTree tree) {
		new ParseTreeWalker().walk(this, tree);
		if (errors != 0) {
			return null;
		} else {
			return tree;
		}
	}
}
