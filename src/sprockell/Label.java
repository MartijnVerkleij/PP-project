package sprockell;

public class Label {
	int lineNumber;
	String name;
	
	public Label(String name, int line) {
		this.name = name;
		this.lineNumber = line;
	}
	
	public String toIR() {
		return "#temp#"+ lineNumber;
	}
}
