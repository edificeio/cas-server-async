package fr.wseduc.cas.exceptions;

public class ValidationException extends Exception {

	private final ErrorCodes error;

	public ValidationException(ErrorCodes error) {
		super(error.getMessage());
		this.error = error;
	}

	public ValidationException(ErrorCodes error, String message) {
		super(message);
		this.error = error;
	}

	public ErrorCodes getError() {
		return error;
	}

}
