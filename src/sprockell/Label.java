package sprockell;

import grammar.exception.CompileException;

public class Label {
	Integer lineNumber;
	String name;

	public Label(String name, int line) {
		this.name = name;
		this.lineNumber = line;
	}

	public Label(String name) {
		this.name = name;
	}

	public void addLineNum(int lineNumber) {
		try {
			if (this.lineNumber == null) {
				this.lineNumber = lineNumber;
			} else {
				throw new CompileException("Line number was already declared");
			}
		} catch (CompileException e) {
			e.printStackTrace();
		}
	}

	public String toIR() {
		return "#temp#" + name;
	}
}
