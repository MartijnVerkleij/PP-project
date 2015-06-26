package grammar;

import grammar.exception.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class PP07Compiler {

	private final static String BASE_DIR = "src/sample/";
	private final static String EXT = ".pp07";
	
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Usage: filename");
		} else {
			try {
				ParseTree tree = parse(new ANTLRInputStream(new FileReader(new File(BASE_DIR + args[0] + EXT))));
				Result checked = new PP07Checker().check(tree);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	

	private static ParseTree parse(CharStream chars) throws ParseException {
		Lexer lexer = new GrammarLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		GrammarParser parser = new GrammarParser(tokens);
		ParseTree result = parser.program();
		return result;
	}
	
}
