package fr.wseduc.cas.exceptions;

public class Try<E extends Throwable, T> {

	private final E exception;
	private final T value;

	public Try(E exception) {
		this.exception = exception;
		this.value = null;
	}

	public Try(T value) {
		this.exception = null;
		this.value = value;
	}

	public T get() throws E {
		if(exception == null) {
			return value;
		}
		throw exception;
	}

	public boolean isSuccess() {
		return exception == null;
	}

	public boolean isFailure() {
		return !isSuccess();
	}

}
