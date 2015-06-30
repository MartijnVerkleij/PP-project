package sprockell;

public class Label {
	
	int id;
	String name;
	
	public Label(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public String toIR() {
		return "#temp#"+ id;
	}
}
