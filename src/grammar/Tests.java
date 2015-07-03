package grammar;


import grammar.exception.ParseException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Tests {
	private final static String BASE_DIR = "src/testfiles/";
	private final static String EXT = ".pp07";

	private static ParseTree parse(CharStream chars) throws ParseException {
		Lexer lexer = new GrammarLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		GrammarParser parser = new GrammarParser(tokens);
		return parser.program();
	}

	@Test
	public void testSyntax() {
		try {
			runChecker("syntax0");
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		try {
//			runChecker("syntax1");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		try {
//			runChecker("syntax2");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		try {
//			runChecker("syntax3");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		try {
//			runChecker("syntax4");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
	}

//	@Test
//	public void testContext() {
//		try {
//			runChecker("context0");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		try {
//			runChecker("context1");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		try {
//			runChecker("context2");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		try {
//			runChecker("context3");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		try {
//			runChecker("context4");
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	}

	@Test
	public void testSemantic() {
		/* TODO: Manually test the following files:
		* semantics0 -> should print:
		* semantics1 -> should print:
		* semantics2 -> should print:
		*/
	}

	private void runChecker(String file) throws ParseException {
		ParseTree tree = null;
		try {
			tree = parse(new ANTLRInputStream(new FileReader(new File(BASE_DIR + file + EXT))));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		new PP07Checker().check(tree);
	}

	private void runGenerator(String file) {
		try {
			ParseTree tree = parse(new ANTLRInputStream(new FileReader(new File(BASE_DIR + file + EXT))));
			runChecker(file);
			new PP07Generator().generate(tree);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
}