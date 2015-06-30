package grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Runs {

	private Map<String, Type> runs = new HashMap<String, Type>();
	
	public boolean addRun(String id, Type type) {
		if (runs.containsKey(id)) {
			return false;
		} else {
			runs.put(id, type);
			return true;
		}
	}
	
	public boolean hasRun(String id) {
		return runs.containsKey(id);
	}
	
	public Type getType(String id) {
		if (runs.containsKey(id)) {
			return runs.get(id);
		}
		return null;
	}
}
