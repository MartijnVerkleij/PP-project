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

/**
 * Compiler for PP07. Generates SPRiL code for use in the Sprockell
 * processor. 
 * @author tim, martijn
 *
 */
public class PP07Compiler {

	private final static String BASE_DIR = "src/sample/";
	private final static String EXT = ".pp07";
	
	
	/**
	 * Compiles PP07 code to SPRiL code. Code is taken from 
	 * /src/sample/\<input\>.pp07 and saved in 
	 * /sprockell/src/program.hs.
	 * @param args name of file in folder /src/sample/, without extension.
	 */
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Usage: filename");
		} else {
			try {
				ParseTree tree = parse(new ANTLRInputStream(new FileReader(new File(BASE_DIR + args[0] + EXT))));

				new PP07Checker().check(tree);
				new PP07Generator().generate(tree);
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Parses an ANTLRInputStream to a ParseTree.
	 * @param chars input stream created from an ANTLRInputStream.
	 * @return ParseTree for use in the 
	 * @throws ParseException
	 */
	private static ParseTree parse(CharStream chars) throws ParseException {
		Lexer lexer = new GrammarLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		GrammarParser parser = new GrammarParser(tokens);
		ParseTree result = parser.program();
		return result;
	}
	
}
