package grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List of declared run statements. Useful for checking whether a 
 * given join statement has a run statement which it can join on. 
 * @author tim, martijn
 *
 */
public class Runs {

	/**
	 * Mapping of runs to Types. The Types are the return values
	 * of the function the run runs.
	 */
	private Map<String, Type> runs = new HashMap<String, Type>();
	
	/**
	 * Add a run, a parallel execution of a Function, to the list of 
	 * known Runs.
	 * @param id unique identifier for the run statement. given by useer input.
	 * @param type return type of the function that is executed with this run.
	 * @return True if the run is added successfully.
	 * False if a run with the given ID already exists. 
	 */
	public boolean addRun(String id, Type type) {
		if (runs.containsKey(id)) {
			return false;
		} else {
			runs.put(id, type);
			return true;
		}
	}
	
	/**
	 * Check whether a run with the given ID is known
	 * @param id identifier of the run requested
	 * @return True if the run is known, false otherwise.
	 */
	public boolean hasRun(String id) {
		return runs.containsKey(id);
	}
	
	/**
	 * Grab the return type of the run with the given ID
	 * @param id identifier of the run
	 * @return the Type of what the run's Function returns, or null if 
	 * no run with the given name was found.
	 */
	public Type getType(String id) {
		if (runs.containsKey(id)) {
			return runs.get(id);
		}
		return null;
	}
}
