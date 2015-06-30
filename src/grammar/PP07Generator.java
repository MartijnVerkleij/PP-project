package grammar;


import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import sprockell.Label;
import sprockell.Op;
import sprockell.Register;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

public class PP07Generator extends GrammarBaseVisitor {
	private BufferedWriter writer;
	private BufferedReader reader;
	private File file;
	private Result checkResult;
	private ParseTreeProperty<Register> regs;
	private ParseTreeProperty<Label> labels;
	private int regCount;

	public File generate(ParseTree tree, Result checkResult) {
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.labels = new ParseTreeProperty<>();
		this.regCount = 0;
		try {

		}

		
		return file;
	}


	private void emit(Op op, String... strings) {
		emit(null, op, strings);
	}

	private void emit(Label label, Op op, String[] strings) {

	}
}
