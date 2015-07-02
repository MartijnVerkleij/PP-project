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
	private final Integer SHARED_MEM = 0;
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
		String id = ctx.ID().getText();
		Type type = getType(ctx.type());
		Integer value = DEFAULT_VALUE;
		if (ctx.ASS() != null) {
			value = visit(ctx.expr());
		}

		if (ctx.GLOBAL() == null) {
			symbolTable.add(id, type);
		} else {
			symbolTable.addGlobal(id, type);
		}

		try {
			emit(OpCode.Const, value.toString(), Indexes.RegA.toString());
			if (ctx.GLOBAL() == null) {
				emit(OpCode.Push, Indexes.RegA.toString());
			} else {
				emit(OpCode.Write, Indexes.RegA.toString(), ((Integer) (SHARED_MEM + symbolTable.offset(id))).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public Integer visitAssStat(@NotNull GrammarParser.AssStatContext ctx) {
		String id = ctx.ID().getText();
		Integer value = visit(ctx.expr());

		try {
			emit(OpCode.Const, value.toString(), Indexes.RegA.toString());
			if (symbolTable.isGlobal(id)) {
				emit(OpCode.Write, Indexes.RegA.toString(), ((Integer) (SHARED_MEM + symbolTable.offset(id))).toString());
			} else {
				emit(OpCode.Store, Indexes.RegA.toString(), ((Integer) (symbolTable.arp(id) + symbolTable.offset(id))).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return value;
	}

	@Override
	public Integer visitEnumStat(@NotNull GrammarParser.EnumStatContext ctx) {
		// TODO
		return null;
	}

	@Override
	public Integer visitIfStat(@NotNull GrammarParser.IfStatContext ctx) {
		return super.visitIfStat(ctx);
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

	/**
	 * Returns the type of a given expression or type node.
	 */
	private Type getType(ParseTree node) {
		return this.checkResult.getType(node);
	}
}
