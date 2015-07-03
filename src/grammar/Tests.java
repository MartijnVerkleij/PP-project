package grammar;


import grammar.exception.ParseException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Tests {
	private final static String BASE_DIR = "src/testfiles/";
	private final static String EXT = ".pp07";

	private static ParseTree parse(CharStream chars) throws ParseException {
		Lexer lexer = new GrammarLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		GrammarParser parser = new GrammarParser(tokens);
		ParseTree result = parser.program();
		return result;
	}

	@Test
	public void testSyntax() {
		List<String> errors;
		errors = runChecker("syntax0");
		assertTrue(errors.isEmpty());
		errors = runChecker("syntax1");
		assertFalse(errors.isEmpty());
		errors = runChecker("syntax2");
		assertFalse(errors.isEmpty());
	}

	@Test
	public void testContext() {
		List<String> errors;
		errors = runChecker("context0");
		assertTrue(errors.isEmpty());
		errors = runChecker("context1");
		assertFalse(errors.isEmpty());
		errors = runChecker("context2");
		assertFalse(errors.isEmpty());
	}

	@Test
	public void testSemantic() {
		/* TODO: Manually test the following files:
		* semantics0 -> should print:
		* semantics1 -> should print:
		* semantics2 -> should print:
		*/
	}

	private List<String> runChecker(String file) {
		List<String> errors = null;
		try {
			ParseTree tree = parse(new ANTLRInputStream(new FileReader(new File(BASE_DIR + file + EXT))));
			errors = new PP07Checker().check(tree);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return errors;
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