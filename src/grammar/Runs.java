package grammar;

import java.util.ArrayList;
import java.util.List;

public class Runs {

	List<String> runs = new ArrayList<String>();
	
	public boolean addRun(String id) {
		if (runs.contains(id)) {
			return false;
		} else {
			runs.add(id);
			return true;
		}
	}
	
	public boolean hasRun(String id) {
		return runs.contains(id);
	}
}
