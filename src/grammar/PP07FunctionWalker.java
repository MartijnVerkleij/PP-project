package grammar;

import grammar.GrammarParser.FuncStatContext;
import grammar.GrammarParser.TypeContext;
import grammar.Type.Types;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class PP07FunctionWalker extends GrammarBaseListener {

	private Functions functions = new Functions();
	
	
	public Functions walkFunctions(ParseTree tree) {
		new ParseTreeWalker().walk(this, tree);
		
		return functions;
	}
	
	@Override
	public void enterFuncStat(FuncStatContext ctx) {
		Types[] arguments = new Types[ctx.ID().size() - 1];
		for (int i = 0; i < ctx.ID().size() - 1; i++) {
			arguments[i] = getType(ctx.type( i + 1));
		}
		functions.addFunction(ctx.ID(0).getText(), arguments);
		
	}
	
	
	public Types getType(TypeContext ctx) {
		if (ctx.getToken(GrammarParser.BOOL, 0) != null) return Types.BOOL;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Types.INT;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Types.VOID;
		return null;
	}
	
	
}
