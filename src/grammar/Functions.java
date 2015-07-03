package grammar;


import grammar.GrammarParser.FuncStatContext;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Models Functions within the programming language, for use 
 * in the checker phase to aid in type checking, argument
 * count- and type checking.
 * @author martijn
 *
 */

public class Functions {

	/**
	 * Holds all Function objects
	 */
	List<Function> functions = new ArrayList<Function>();
	
	/**
	 * Add a new Function to the list of declared functions.
	 * @param name Name of the function
	 * @param rettype Return type of the function
	 * @param args Arguments of the function
	 * @return True when the function was successfully added,
	 * False when the function name was already declared.
	 */
	public boolean addFunction(String name, Type rettype, Type ... args) {
		if (getFunction(name) == null) {
			functions.add(new Function(name, rettype, args));
			return true;
		}
		return false;
	}
	
	/**
	 * Retrieve the Function object associated with the given name.
	 * @param name name of the requested function
	 * @return Requested function object, or null if the function 
	 * is not defined.
	 */
	public Function getFunction(String name) {
		for (Function function : functions) {
			if (function.getName().equals(name)) {
				return function;
			}
		}
		return null;
	}
	
	/**
	 * Check if a function with the given name is defined.
	 * @param name name to check
	 * @return True if a function was found with the given name
	 * False if none was found.
	 */
	public boolean hasFunction(String name) {
		for (Function function : functions) {
			if (function.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Function object to model a Function in the context of code checking.
	 * Inner defined class.
	 * @author martijn
	 *
	 */
	public class Function {
		/**
		 * Name of the function
		 */
		private String name;
		
		/**
		 * Return type of the function
		 */
		private Type returntype;
		
		/**
		 * Arguments of the function
		 */
		private List<Type> arguments = new ArrayList<Type>();
		
		/**
		 * Context in which the Function occurs, useful for Control 
		 * Flow Graph generation. Can be set arbitrarily.
		 */
		private FuncStatContext context;
		
		
		/**
		 * Creates a Fucntion with the given parameters.
		 * @param name name of the function
		 * @param rettype return type of the function
		 * @param args types of the arguments of the function
		 */
		public Function(String name, Type rettype, Type ... args) {
			this.name = name;
			this.returntype = rettype;
			for (Type arg : args) {
				arguments.add(arg);
			}
		}
		
		/**
		 * Returns the name of the Function
		 * @return function name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Returns the number of arguments this function has
		 * @return argument count
		 */
		public int getArgumentCount() {
			return arguments.size();
		}
		
		/**
		 * Get the i'th argument's type
		 * @param i index of the requested argument. This starts at 0
		 * @return type of argument that was requested.
		 */
		//@requires i < getArguments().size()
		public Type getArgument(int i) {
			return arguments.get(i);
		}
		
		/**
		 * Get internal list of Arguments as an ArrayList. for use in 
		 * JML
		 * @return ArrayList of arguments.
		 */
		//@pure
		public List<Type> getArguments() {
			return arguments;
		}
		
		/**
		 * Set the context of this Function
		 * @param context Function node's context.
		 */
		public void setContext(FuncStatContext context) {
			this.context = context;
		}
		
		/**
		 * Retrieve the function's context.
		 * @return Context of the Function
		 */
		public FuncStatContext getContext() {
			return context;
		}
		
		/**
		 * Get the return type of this Function
		 * @return return type
		 */
		public Type getReturntype() {
			return returntype;
		}
	}
}
