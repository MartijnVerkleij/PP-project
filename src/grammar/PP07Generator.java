package grammar;


import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import sprockell.Label;
import sprockell.OpCode;
import sprockell.Register.Indexes;

import java.io.*;
import java.util.ArrayList;

/* CORE ARP
return value			-- global
return value boolean	-- global
arp						-- global
*return address			-- local (global)
start address			-- global
thread lock				-- global
 */

public class PP07Generator extends GrammarBaseVisitor<Integer> {
	private final Integer DEFAULT_VALUE = 0;
	private final Integer STD_IO = 0x1000000;
	private final Integer SHARED_IN = 0xFFFFFF;
	private final Integer SHARED_OUT = 0;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Result checkResult;
	private SymbolTable symbolTable;
	private ArrayList<Label> labels;
	private int lineNum;

	public File generate(ParseTree tree, Result checkResult) {
		this.checkResult = checkResult;
		this.symbolTable = new SymbolTable();
		this.labels = new ArrayList<>();
		this.lineNum = 0;
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

	@Override
	public Integer visitProgram(@NotNull GrammarParser.ProgramContext ctx) {
		ctx.stat().forEach(this::visit);
		return null;
	}

	@Override
	public Integer visitDeclStat(@NotNull GrammarParser.DeclStatContext ctx) {
		String value = DEFAULT_VALUE.toString();
		if (ctx.ASS() != null) {
			value = visit(ctx.expr()).toString();
		}
		try {
			emit(OpCode.Const, value, Indexes.RegA.toString());
			if (ctx.GLOBAL() == null) {
				emit(OpCode.Store, Indexes.RegA.toString(), symbolTable.offset(ctx.ID().getText()).toString());
			} else {

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void emit(OpCode opCode, String... strings) throws IOException {
		emit(null, opCode, strings);
	}

	private void emit(String label, OpCode opCode, String[] strings) throws IOException {
		String operands = "";
		for (String string : strings) {
			operands += string + " ";
		}
		if (label != null) {
			labels.add(new Label(label, lineNum++));
		}
		writer.write(opCode.toString() + " " + operands);
		writer.newLine();
	}
}
