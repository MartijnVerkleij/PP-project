package grammar;


import grammar.exception.ParseException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Assert;
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
			Assert.fail();
		}
		try {
			runChecker("syntax1");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			runChecker("syntax2");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			runChecker("syntax3");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			runChecker("syntax4");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testContext() {
		try {
			runChecker("context0");
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
		try {
			runChecker("context1");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			runChecker("context2");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			runChecker("context3");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			runChecker("context4");
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSemantic() {
		/* TODO: Manually test the following files:
		* semantics0 -> should print: @
		* semantics1 -> should print: P
		* semantics2 -> should get stuck in an infinite loop
		* semantics3 -> should crash because of division by 0
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