package grammar.exception;

public class ParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8720003723752395545L;

	private String message;
	
	public ParseException(String detail) {
		this.message = detail;
	}
	
	public String getMessage() {
		return message;
	}
}
