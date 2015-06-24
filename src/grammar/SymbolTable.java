package grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {

	// Global Scope
	private Map<String, Type> globalTypes = new HashMap<String, Type>();
	private Map<String, Integer> globalOffsets = new HashMap<>();
	private int globalSize = 0;
	// Local Scopes
	private Stack<Map<String, Integer>> offsets = new Stack<>();
	private Stack<Integer> size = new Stack<>();
	private Stack<Map<String, Type>> types = new Stack<Map<String, Type>>();

	/**
	 * Adds a next deeper scope level.
	 */
	public void openScope() {
		types.add(new HashMap<String, Type>());
		offsets.add(new HashMap<String, Integer>());
		size.add(0);
	}

	/**
	 * Removes the deepest scope level.
	 *
	 * @throws RuntimeException if the table only type the outer scope.
	 */
	public void closeScope() {
		types.pop();
		offsets.pop();
		size.pop();
	}

	/**
	 * Tries to declare a given identifier in the globalTypes scope.
	 *
	 * @param id   name of the identifier
	 * @param type type of the identifier
	 * @return <code>true</code> if the identifier was added,
	 * <code>false</code> if it was already declared in this scope.
	 */
	public boolean addGlobal(String id, Type type) {
		if (!globalTypes.containsKey(id)) {
			globalTypes.put(id, type);
			globalOffsets.put(id, this.globalSize);
			globalSize += type.size();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tries to declare a given identifier in the deepest scope level.
	 *
	 * @param id   name of the identifier
	 * @param type type of the identifier
	 * @return <code>true</code> if the identifier was added,
	 * <code>false</code> if it was already declared in this scope.
	 */
	public boolean add(String id, Type type) {
		boolean isGood = false;
		if (!types.isEmpty() && !types.peek().containsKey(id)) {
			types.peek().put(id, type);
			offsets.peek().put(id, this.size.peek());
			int tempSize = size.pop() + type.size();
			size.push(tempSize);
			isGood = true;
		}
		return isGood;
	}

	/**
	 * Returns the type of an identifier at the deepest level of the scope
	 * where it is declared.
	 *
	 * @param id name of the identifier
	 * @return <code>Type</code> if there is any enclosing scope in which
	 * the identifier is declared; <code>null</code> otherwise.
	 */
	public Type type(String id) {
		Type contained = null;
		for (int i = types.size() - 1; i >= 0; i--) {
			if (types.get(i).containsKey(id)) {
				contained = types.get(i).get(id);
				break;
			}
		}
		if (contained == null) {
			contained = globalTypes.containsKey(id) ? globalTypes.get(id) : null;
		}
		return contained;
	}

	/**
	 * Returns the offset of an identifier at the deepest level of the scope
	 * where it is declared.
	 *
	 * @param id name of the identifier
	 * @return <code>Integer</code> if there is any enclosing scope in which
	 * the identifier is declared; <code>null</code> otherwise.
	 */
	public Integer offset(String id) {
		Integer offset = null;
		for (int i = offsets.size() - 1; i >= 0; i--) {
			if (offsets.get(i).containsKey(id)) {
				offset = offsets.get(i).get(id);
				break;
			}
		}
		if (offset == null) {
			offset = globalOffsets.containsKey(id) ? globalOffsets.get(id) : null;
		}
		return offset;
	}

}
