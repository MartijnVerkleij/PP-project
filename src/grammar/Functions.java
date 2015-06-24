package grammar;

import grammar.Type.Types;

import java.util.ArrayList;
import java.util.List;

public class Functions {

	List<Function> functions = new ArrayList<Function>();
	
	public boolean addFunction(String name, Types ... args) {
		if (getFunction(name) != null) {
			functions.add(new Function(name, args));
			return true;
		}
		return false;
	}
	
	public Function getFunction(String name) {
		for (Function function : functions) {
			if (function.getName().equals(name)) {
				return function;
			}
		}
		return null;
	}
	
	public boolean hasFunction(String name) {
		for (Function function : functions) {
			if (function.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public class Function {
		private String name;
		private List<Types> arguments = new ArrayList<Types>();
		
		public Function(String name, Types ... args) {
			this.name = name;
			for (Types arg : args) {
				arguments.add(arg);
			}
		}
		
		public String getName() {
			return name;
		}
		
		public int getArgumentCount() {
			return arguments.size();
		}
		
		public Types getArgument(int i) {
			return arguments.get(i);
		}
	}
}
