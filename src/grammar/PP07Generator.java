package grammar;


import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import sprockell.Label;
import sprockell.OpCode;
import sprockell.Register;

import java.io.*;

public class PP07Generator extends GrammarBaseVisitor {
	private BufferedWriter writer;
	private BufferedReader reader;
	private Result checkResult;
	private ParseTreeProperty<Register> regs;
	private ParseTreeProperty<Label> labels;
	private int regCount;

	public File generate(ParseTree tree, Result checkResult) {
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.labels = new ParseTreeProperty<>();
		this.regCount = 0;
		File file = new File("program.hs");
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			writer = new BufferedWriter(fw);
			generateHeader();
			tree.accept(this);
			writer.flush();
			writer.close();
			FileReader fr = new FileReader(file);
			reader = new BufferedReader(fr);
			generateLabels();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return file;
	}

	private void generateLabels() {

	}

	private void generateHeader() {

	}


	private void emit(OpCode opCode, String... strings) throws IOException {
		emit(null, opCode, strings);
	}

	private void emit(Label label, OpCode opCode, String[] strings) throws IOException {
		String operands = "";
		for (String string : strings) {
			operands += string + " ";
		}
		writer.write(label.toIR() + " " + opCode.toString() + " " + operands);
		writer.newLine();
	}
}
