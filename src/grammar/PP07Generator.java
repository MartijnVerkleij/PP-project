package grammar;


import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import sprockell.Label;
import sprockell.OpCode;
import sprockell.Register.Indexes;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/* CORE ARP
return value			-- global
return value boolean	-- global
arp						-- global
*return address			-- local (global)
start address			-- global
thread lock				-- global
 */

public class PP07Generator extends GrammarBaseVisitor<Op> {
	private final Value DEFAULT_VALUE = new Value(0);
	private final Integer STD_IO = 0x1000000;
	private final Type type = Type.INT;
	private BufferedWriter writer;
	private BufferedReader reader;
	private SymbolTable symbolTable;
	private Map<String, Label> labels;
	private int lineNum;
	private int labelID = 0;
	private String endProgLabel;

	public File generate(ParseTree tree) {
		this.symbolTable = new SymbolTable();
		this.labels = new HashMap<>();
		this.lineNum = 0;
		this.labelID = 0;
		this.endProgLabel = getNewLabelID();
		File temp = new File("temp.hs");
		File file = new File("sprockell/src/program.hs");
		try {
			temp.createNewFile();
			writer = new BufferedWriter(new FileWriter(temp));
			generateHeader();
			tree.accept(this);
			generateFooter();
			writer.flush();
			writer.close();
			writer = new BufferedWriter(new FileWriter(file));
			file.createNewFile();
			reader = new BufferedReader(new FileReader(temp));
			generateLabels();
			reader.close();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return file;
	}

	private void generateLabels() throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			int first = line.indexOf("#temp#");
			int second = line.lastIndexOf("#temp#") + 6;
			if (first >= 0) {
				line = line.replaceAll("#temp#(.*?)#temp#", labels.get(line.substring(first, second)).getLine());
			}
			writer.write(line);
			writer.newLine();
		}
	}


	private void generateHeader() throws IOException {
		String[] header = new String[]{
				"import Sprockell.System",
				"prog :: [Instruction]",
				"prog = ["
		};
		for (String s : header) {
			writer.write(s);
			writer.newLine();
//			lineNum++;
		}
	}

	private void generateFooter() throws IOException {
		String[] footer = new String[]{
				"\tRead (Addr 0) ,",
				"\tReceive RegA ,",
				"\tWrite RegA stdio ,",
				"\tRead (Addr 0x0) ,",
				"\tReceive RegA ,",
				"\tEndProg",
				"\t]",
				"main = run 1 prog >> putChar '\\n'"
		};
		for (String s : footer) {
			if (s.equals("\tEndProg")) {
				labels.put(endProgLabel, new Label(endProgLabel, lineNum));
			}
			writer.write(s);
			writer.newLine();
			lineNum++;
		}
	}

	@Override
	public Op visitProgram(@NotNull GrammarParser.ProgramContext ctx) {
		ctx.stat().forEach(this::visit);
		return null;
	}

	@Override
	public Op visitDeclStat(@NotNull GrammarParser.DeclStatContext ctx) {
		String id = ctx.ID().getText();
		Value value = DEFAULT_VALUE;
		if (ctx.ASS() == null) {
			emit(OpCode.Const, value.toString(), Indexes.RegA.toString());
		} else {
			visit(ctx.expr());
			emit(OpCode.Pop, Indexes.RegA.toString());
		}

		if (ctx.GLOBAL() == null) {
			symbolTable.add(id, type);
			emit(OpCode.Store, Indexes.RegA.toString(), "(Addr " + (symbolTable.arp(id) + symbolTable.offset(id)) + ")");
		} else {
			symbolTable.addGlobal(id, type);
			emit(OpCode.Write, Indexes.RegA.toString(), "(Addr " + symbolTable.offset(id) + ")");
		}
		return null;
	}

	@Override
	public Op visitAssStat(@NotNull GrammarParser.AssStatContext ctx) {
		String id = ctx.ID().getText();
		visit(ctx.expr());
		emit(OpCode.Pop, Indexes.RegA.toString());
		if (symbolTable.isGlobal(id)) {
			emit(OpCode.Write, Indexes.RegA.toString(), "(Addr " + symbolTable.offset(id) + ")");
		} else {
			emit(OpCode.Store, Indexes.RegA.toString(), "(Addr " + (symbolTable.arp(id) + symbolTable.offset(id)) + ")");
		}
		return null;
	}

	@Override
	public Op visitEnumStat(@NotNull GrammarParser.EnumStatContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitIfStat(@NotNull GrammarParser.IfStatContext ctx) {
		visit(ctx.expr());
		emit(OpCode.Pop, Indexes.RegA.toString()); // value to compare

		emit(OpCode.Compute, "Equal", Indexes.RegA.toString(), Indexes.Zero.toString(),
				Indexes.RegA.toString()); // compare

		String endLabel = getNewLabelID(); // label for jump to end

		if (ctx.ELSE() == null) {
			// jump to end or continue
			emit(OpCode.Branch, Indexes.RegA.toString(), "(Abs " + endLabel + ")");
			visit(ctx.block(0));
		} else {
			String elseLabel = getNewLabelID(); // label for jump to else

			emit(OpCode.Branch, Indexes.RegA.toString(), "(Abs " + elseLabel + ")");
			visit(ctx.block(0));
			emit(OpCode.Jump, "(Abs " + endLabel + ")");
			emit(elseLabel, OpCode.Nop);
			visit(ctx.block(1));
		}
		emit(endLabel, OpCode.Nop);
		return null;
	}

	@Override
	public Op visitWhileStat(@NotNull GrammarParser.WhileStatContext ctx) {
		// Labels
		String beginLabel = getNewLabelID();
		String checkLabel = getNewLabelID();

		emit(OpCode.Jump, "(Abs " + checkLabel + ")"); // jump to check
		emit(beginLabel, OpCode.Nop); // set begin label
		visit(ctx.block()); // content of while

		emit(checkLabel, OpCode.Nop); // set begin label
		visit(ctx.expr()); // evaluate expression
		emit(OpCode.Pop, Indexes.RegA.toString()); // value to compare
		emit(OpCode.Const, "1", Indexes.RegB.toString()); // constant reg with 1
		emit(OpCode.Compute, "Equal", Indexes.RegA.toString(), Indexes.RegB.toString(),
				Indexes.RegA.toString()); // compare
		emit(OpCode.Branch, Indexes.RegA.toString(), "(Abs " + beginLabel + ")"); // branch
		return null;
	}

	@Override
	public Op visitBlockStat(@NotNull GrammarParser.BlockStatContext ctx) {
		visit(ctx.block());
		return null;
	}

	@Override
	public Op visitFuncStat(@NotNull GrammarParser.FuncStatContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitExprStat(@NotNull GrammarParser.ExprStatContext ctx) {
		visit(ctx.expr());
		emit(OpCode.Pop, Indexes.Zero.toString());
		return null;
	}

	@Override
	public Op visitRunStat(@NotNull GrammarParser.RunStatContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitLockStat(@NotNull GrammarParser.LockStatContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitUnlockStat(@NotNull GrammarParser.UnlockStatContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitReturnStat(@NotNull GrammarParser.ReturnStatContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitBlock(@NotNull GrammarParser.BlockContext ctx) {
		if (ctx.getParent().getParent() instanceof GrammarParser.FuncStatContext) {
			symbolTable.openScope(((GrammarParser.FuncStatContext) ctx.getParent().getParent()).ID().size());
		} else {
			symbolTable.openScope();
		}
		ctx.stat().forEach(this::visit);
		symbolTable.closeScope();
		return null;
	}

	@Override
	public Op visitFuncCall(@NotNull GrammarParser.FuncCallContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitJoinExpr(@NotNull GrammarParser.JoinExprContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitLockedExpr(@NotNull GrammarParser.LockedExprContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitPlusExpr(@NotNull GrammarParser.PlusExprContext ctx) {
		visit(ctx.expr(1));
		visit(ctx.expr(0));
		emit(OpCode.Pop, Indexes.RegA.toString());
		emit(OpCode.Pop, Indexes.RegB.toString());
		if (ctx.plusOp().PLUS() == null) {
			emit(OpCode.Compute, "Sub", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else {
			emit(OpCode.Compute, "Add", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		}
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitMultExpr(@NotNull GrammarParser.MultExprContext ctx) {
		visit(ctx.expr(1));
		visit(ctx.expr(0));
		emit(OpCode.Pop, Indexes.RegA.toString());
		emit(OpCode.Pop, Indexes.RegB.toString());
		if (ctx.multOp().STAR() == null) {
			emit(OpCode.Compute, "Div", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else {
			emit(OpCode.Compute, "Mul", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		}
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	// Very inefficient because of bad thinking during heat, may revisit later
	@Override
	public Op visitExpExpr(@NotNull GrammarParser.ExpExprContext ctx) {
		// Labels
		String zeroLabel = getNewLabelID();
		String endLabel = getNewLabelID();

		// Evaluating expressions
		visit(ctx.expr(1));
		visit(ctx.expr(0));
		// Saving expressions to registers
		emit(OpCode.Pop, Indexes.RegA.toString()); // value to multiply
		emit(OpCode.Pop, Indexes.RegB.toString()); // times to multiply

		// Checking for 0 in RegB
		emit(OpCode.Compute, "Equal", Indexes.RegB.toString(), Indexes.Zero.toString(), Indexes.RegD.toString());
		emit(OpCode.Branch, Indexes.RegD.toString(), "(Abs " + zeroLabel + ")");

		// Continue if not zero
		emit(OpCode.Const, "1", Indexes.RegC.toString()); // constant reg with 1, used for compare
		emit(OpCode.Const, "1", Indexes.RegE.toString()); // constant reg with 1, used for result
		{ // Block to clarify, while loop in here
			// More Labels
			String beginLabel = getNewLabelID();
			String checkLabel = getNewLabelID();

			emit(OpCode.Jump, "(Abs " + checkLabel + ")"); // jump to check

			// Content of while
			emit(beginLabel, OpCode.Compute, "Mul", Indexes.RegA.toString(), Indexes.RegE.toString(),
					Indexes.RegE.toString());
			emit(OpCode.Compute, "Sub", Indexes.RegB.toString(), Indexes.RegC.toString(), Indexes.RegB.toString());

			// Checking part
			emit(checkLabel, OpCode.Compute, "GtE", Indexes.RegB.toString(), Indexes.RegC.toString(),
					Indexes.RegD.toString());
			emit(OpCode.Branch, Indexes.RegD.toString(), "(Abs " + beginLabel + ")");
		}
		emit(OpCode.Jump, "(Abs " + endLabel + ")");

		// Jump target if zero
		emit(zeroLabel, OpCode.Const, "1", Indexes.RegE.toString()); // constant with 1, for returning

		// End
		emit(endLabel, OpCode.Push, Indexes.RegE.toString());

		return null;
	}

	@Override
	public Op visitBoolExpr(@NotNull GrammarParser.BoolExprContext ctx) {
		visit(ctx.expr(1));
		visit(ctx.expr(0));
		emit(OpCode.Pop, Indexes.RegA.toString());
		emit(OpCode.Pop, Indexes.RegB.toString());
		if (ctx.boolOp().AND() == null) {
			emit(OpCode.Compute, "Or", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else {
			emit(OpCode.Compute, "And", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		}
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitCmpExpr(@NotNull GrammarParser.CmpExprContext ctx) {
		visit(ctx.expr(1));
		visit(ctx.expr(0));
		emit(OpCode.Pop, Indexes.RegA.toString());
		emit(OpCode.Pop, Indexes.RegB.toString());
		if (ctx.cmpOp().EQ() != null) {
			emit(OpCode.Compute, "Equal", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else if (ctx.cmpOp().NE() != null) {
			emit(OpCode.Compute, "NEq", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else if (ctx.cmpOp().GT() != null) {
			emit(OpCode.Compute, "Gt", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else if (ctx.cmpOp().GE() != null) {
			emit(OpCode.Compute, "GtE", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else if (ctx.cmpOp().LT() != null) {
			emit(OpCode.Compute, "Lt", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		} else if (ctx.cmpOp().LE() != null) {
			emit(OpCode.Compute, "LtE", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		}
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitPrfExpr(@NotNull GrammarParser.PrfExprContext ctx) {
		visit(ctx.expr());
		emit(OpCode.Pop, Indexes.RegA.toString());
		if (ctx.prfOp().MINUS() == null) {
			emit(OpCode.Compute, "Sub", Indexes.Zero.toString(), Indexes.RegA.toString(), Indexes.RegA.toString());
		} else {
			emit(OpCode.Const, "1", Indexes.RegB.toString()); // constant reg with 1, used for XOR
			emit(OpCode.Compute, "Xor", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		}
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitParExpr(@NotNull GrammarParser.ParExprContext ctx) {
		visit(ctx.expr());
		return null;
	}

	@Override
	public Op visitIdExpr(@NotNull GrammarParser.IdExprContext ctx) {
		String id = ctx.ID().getText();
		if (symbolTable.isGlobal(id)) {
			emit(OpCode.Read, "(Addr " + symbolTable.offset(id) + ")");
			emit(OpCode.Receive, Indexes.RegA.toString());
		} else {
			emit(OpCode.Load, "(Addr " + (symbolTable.arp(id) + symbolTable.offset(id)) + ")", Indexes.RegA.toString());
		}
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitNumExpr(@NotNull GrammarParser.NumExprContext ctx) {
		emit(OpCode.Const, ctx.NUM().getText(), Indexes.RegA.toString());
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitEidExpr(@NotNull GrammarParser.EidExprContext ctx) {
		// TODO: Not implemented
		return null;
	}

	@Override
	public Op visitTrueExpr(@NotNull GrammarParser.TrueExprContext ctx) {
		emit(OpCode.Const, "1", Indexes.RegA.toString());
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitFalseExpr(@NotNull GrammarParser.FalseExprContext ctx) {
		emit(OpCode.Const, "0", Indexes.RegA.toString());
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	private void emit(Op op) {
		emit(op.getLabel(), op.getOpCode(), op.getOperands());
	}

	private void emit(OpCode opCode, String... strings) {
		emit(null, opCode, strings);
	}

	private void emit(String label, OpCode opCode, String... strings) {
		String operands = "";
		for (String string : strings) {
			operands += string + " ";
		}
		if (label != null) {
			labels.put(label, new Label(label, lineNum));
		}
		lineNum++;
		try {
			writer.write("\t" + opCode.toString() + " " + operands + ",");
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getNewLabelID() {
		return "#temp#" + labelID++ + "#temp#";
	}
}
