package grammar;

import sprockell.OpCode;

public class Op {
	private String label;
	private OpCode opCode;
	private String[] operands;

	public Op(OpCode opCode, String... operands) {
		this.opCode = opCode;
		this.operands = operands;
	}

	public Op(String label, OpCode opCode, String... operands) {
		this.label = label;
		this.opCode = opCode;
		this.operands = operands;
	}

	public OpCode getOpCode() {
		return opCode;
	}

	public String[] getOperands() {
		return operands;
	}

	public String getLabel() {
		return label;
	}
}
