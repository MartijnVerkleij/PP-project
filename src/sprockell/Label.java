package sprockell;

public class Label {
	
	int id;
	
	public Label(int id) {
		this.id = id;
	}
	
	public String toIR() {
		return "#"+ id +"";
	}
}
