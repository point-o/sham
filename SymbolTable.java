package dsh;

import java.util.HashMap;

/**
 * A class to track variables and registered commands
 * 
 * @author Ryan Pointer
 * @version 6/26/25
 */
public class SymbolTable {
	
	private final HashMap<String, Data> variables = new HashMap<>(); 
	
	//VARIABLES//
	
	/**
	 * Initializes or replaces the value in a variable
	 * @param name - variable name
	 * @param value - the value held by the variable
	 * @throws IllegalArgument Exception if the variable name doesn't exist (for debugging)
	 */
	public void set(String name, Data value) {
		if (name == null || name.trim().isEmpty()){
			throw new IllegalArgumentException("Name DNE");
		}
		variables.put(name, value);
	}
	
	/**
	 * Gets the value stored in the variable
	 * @param name - name of the variable
	 * @return - null if variable doesn't, otherwise the value is returned
	 */
	public Data get(String name) {
		Data value = variables.get(name);
		if (!variables.containsKey(name)){
			System.err.println("[Error] Undefined variable: '" + name + "'");
			return null;
		}
		return variables.get(name);
	}
	
}
