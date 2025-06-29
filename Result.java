package dsh;

/**
 * A class for logging command results
 * 
 * @author - Ryan Pointer
 * @version - 6/26/25
 */
public class Result {
	private final boolean success;
	private final String message;
	
	/**
	 * Result constructor
	 * @param success - success of result
	 * @param message - message to acompany result (if neccessary)
	 */
	public Result(boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
	/**
	 * Getters
	 */
	public boolean successful() { return success; }
	public String getMessage() { return message; }
	
	/**
	 * For predefined success
	 * @param message - success message (for debug)
	 * @return - Successful Result
	 */
	public static Result success(String message) {
		return new Result(true, message);
	}
	
	/**
	 * For predefined error
	 * @param message - error message
	 * @return - Error Result
	 */
	public static Result error(String message) {
		return new Result(false, "[Error]" + message);
	}
}
