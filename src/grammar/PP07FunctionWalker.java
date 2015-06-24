package grammar;

import grammar.GrammarParser.FuncStatContext;
import grammar.GrammarParser.TypeContext;

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
		Type[] arguments = new Type[ctx.ID().size() - 1];
		for (int i = 0; i < ctx.ID().size() - 1; i++) {
			arguments[i] = getType(ctx.type( i + 1));
		}
		functions.addFunction(ctx.ID(0).getText(), arguments);
		
	}
	
	
	public Type getType(TypeContext ctx) {
		if (ctx.getToken(GrammarParser.BOOL, 0) != null) return Type.BOOL;
		if (ctx.getToken(GrammarParser.INT, 0) != null) return Type.INT;
//		if (ctx.getToken(GrammarParser.INT, 0) != null) return Type.VOID;
		return null;
	}
	
	
}
