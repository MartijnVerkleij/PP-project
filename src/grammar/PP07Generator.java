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

public class PP07Generator extends GrammarBaseVisitor<Op> {
	private final Value DEFAULT_VALUE = new Value(0);
	private final Integer STD_IO = 0x1000000;
	private final Type type = Type.VOID;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Result checkResult;
	private SymbolTable symbolTable;
	private ArrayList<Label> labels;
	private int lineNum;
	private int labelID = 0;

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
			emit(OpCode.Store, Indexes.RegA.toString(), symbolTable.offset(id).toString());
		} else {
			symbolTable.addGlobal(id, type);
			emit(OpCode.Write, Indexes.RegA.toString(), symbolTable.offset(id).toString());
		}
		return null;
	}

	@Override
	public Op visitAssStat(@NotNull GrammarParser.AssStatContext ctx) {
		String id = ctx.ID().getText();
		visit(ctx.expr());
		emit(OpCode.Pop, Indexes.RegA.toString());
		if (symbolTable.isGlobal(id)) {
			emit(OpCode.Write, Indexes.RegA.toString(), symbolTable.offset(id).toString());
		} else {
			emit(OpCode.Store, Indexes.RegA.toString(), symbolTable.offset(id).toString());
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

		emit(OpCode.Const, "0", Indexes.RegB.toString()); // constant reg with 0
		emit(OpCode.Compute, "Equal", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString()); // compare

		if (ctx.ELSE() != null) {
			String endLabel = getNewLabelID() + "endif"; // label for jump to end

			// jump to end or continue
			emit(OpCode.Branch, Indexes.RegA.toString(), endLabel);
			visit(ctx.block(0));
			emit(endLabel, OpCode.Nop);
		} else {
			String elseLabel = getNewLabelID() + "elseif"; // label for jump to else

			emit(OpCode.Branch, Indexes.RegA.toString(), elseLabel);
			visit(ctx.block(0));
			emit(elseLabel, OpCode.Nop);
			visit(ctx.block(1));
		}
		return null;
	}

	@Override
	public Op visitWhileStat(@NotNull GrammarParser.WhileStatContext ctx) {
		// Labels
		String beginLabel = getNewLabelID() + "beginwhile";
		String checkLabel = getNewLabelID() + "checkwhile";

		emit(OpCode.Jump, checkLabel); // jump to check
		emit(beginLabel, OpCode.Nop); // set begin label
		visit(ctx.block()); // content of while

		emit(checkLabel, OpCode.Nop); // set begin label
		visit(ctx.expr()); // evaluate expression
		emit(OpCode.Pop, Indexes.RegA.toString()); // value to compare
		emit(OpCode.Const, "1", Indexes.RegB.toString()); // constant reg with 1
		emit(OpCode.Compute, "Equal", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString()); // compare
		emit(OpCode.Branch, Indexes.RegA.toString(), Indexes.RegB.toString(), beginLabel); // branch
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
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		emit(OpCode.Pop, Indexes.RegA.toString());
		emit(OpCode.Pop, Indexes.RegB.toString());
		emit(OpCode.Compute, "Add", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitMultExpr(@NotNull GrammarParser.MultExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		emit(OpCode.Pop, Indexes.RegA.toString());
		emit(OpCode.Pop, Indexes.RegB.toString());
		emit(OpCode.Compute, "Mul", Indexes.RegA.toString(), Indexes.RegB.toString(), Indexes.RegA.toString());
		emit(OpCode.Push, Indexes.RegA.toString());
		return null;
	}

	@Override
	public Op visitExpExpr(@NotNull GrammarParser.ExpExprContext ctx) {
		return super.visitExpExpr(ctx);
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
			labels.add(new Label(label, lineNum));
		}
		lineNum++;
		try {
			writer.write(opCode.toString() + " " + operands);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getNewLabelID() {
		return labelID++;
	}
}
