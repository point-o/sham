package dsh;

import java.util.List;

/**
 * Represents an executable command.
 * We have 5 general types:
 * 
 * Creation: Variable creation
 * Edit: Variable editing
 * Load: Intaking data
 * Save: Enviornment persistence
 * Filter: Filtering data
 * 
 * Each have general arguments that need to be fulfilled to proceed
 * 
 * The Result type referenced here will give Success/Failure status
 * 
 * @author Ryan Pointer
 * @version 6/26/25
 */
public interface Command {
	Result execute(SymbolTable env, List<String> args);
}
