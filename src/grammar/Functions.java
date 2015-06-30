package grammar;


import grammar.GrammarParser.FuncStatContext;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

public class Functions {

	List<Function> functions = new ArrayList<Function>();
	
	public boolean addFunction(String name, Type rettype, Type ... args) {
		if (getFunction(name) == null) {
			functions.add(new Function(name, rettype, args));
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
		private Type returntype;
		private List<Type> arguments = new ArrayList<Type>();
		private FuncStatContext context;
		
		public Function(String name, Type rettype, Type ... args) {
			this.name = name;
			this.returntype = rettype;
			for (Type arg : args) {
				arguments.add(arg);
			}
		}
		
		public String getName() {
			return name;
		}
		
		public int getArgumentCount() {
			return arguments.size();
		}
		
		public Type getArgument(int i) {
			return arguments.get(i);
		}
		
		public void setContext(FuncStatContext context) {
			this.context = context;
		}
		
		public FuncStatContext getContext() {
			return context;
		}
		
		public Type getReturntype() {
			return returntype;
		}
	}
}
