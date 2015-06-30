package sprockell;

public class Label {
	
	int lineNumber;
	
	public Label(int line) {
		this.lineNumber = line;
	}
	
	public String toIR() {
		return "#"+ lineNumber +"";
	} 
}
