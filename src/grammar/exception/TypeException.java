package grammar.exception;

/**
 * Exception class wrapping an error messages.
 */
public class TypeException extends Exception {
	private final String message;


	public TypeException(String message) {
		super(message);
		this.message = message;
	}

	/**
	 * Returns the error message wrapped in this exception.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Print error message to stdout.
	 */
	public void print() {
		System.out.println(message);
	}
}
