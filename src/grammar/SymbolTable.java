package grammar;

import grammar.Type.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable{

	private Map<String, Types> global = new HashMap<String, Types>();
	private Stack<Map<String, Types>> stack = new Stack<Map<String, Types>>();
	

	/**
	 * Adds a next deeper scope level.
	 */
	public void openScope() {
//		if (!stack.isEmpty()) {
//			stack.add(new HashMap<String, Object>(stack.peek()));
//		} else {
		stack.add(new HashMap<String, Types>());
//		}
	}

	/**
	 * Removes the deepest scope level.
	 *
	 * @throws RuntimeException if the table only contains the outer scope.
	 */
	public void closeScope() {
		stack.pop();
	}

	/**
	 * Tries to declare a given identifier in the global scope.
	 *
	 * @param id
	 * @param type 
	 * @return <code>true</code> if the identifier was added,
	 * <code>false</code> if it was already declared in this scope.
	 */
	public boolean addGlobal(String id, Types type) {
		if (!global.containsKey(id)) {
			global.put(id, type);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Tries to declare a given identifier in the deepest scope level.
	 *
	 * @param id
	 * @param type
	 * @return <code>true</code> if the identifier was added,
	 * <code>false</code> if it was already declared in this scope.
	 */
	public boolean add(String id, Types type) {
		boolean isGood = false;
		if (!stack.isEmpty() && !stack.peek().containsKey(id)) {
			stack.peek().put(id, type);
			isGood = true;
		} /*else if (stack.isEmpty() && global.containsKey(id)) {
			global.put(id, null);
			isGood = true;
		}*/
		return isGood;
	}

	/**
	 * Tests if a given identifier is in the scope of any declaration.
	 *
	 * @param id
	 * @return <code>true</code> if there is any enclosing scope in which
	 * the identifier is declared; <code>false</code> otherwise.
	 */
	public Types contains(String id) {
		Types contained = null;
		for (int i = stack.size() - 1; i >= 0; i--) {
			if (stack.get(i).containsKey(id)) {
				contained = stack.get(i).get(id);
				break;
			}
		}
		if (contained == null) {
			contained = global.containsKey(id) ? global.get(id) : null;
		}
		return contained;
	}
}
