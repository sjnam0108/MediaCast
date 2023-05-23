package net.doohad.exceptions;

@SuppressWarnings("serial")
public class ServerOperationForbiddenException extends RuntimeException {
	public ServerOperationForbiddenException() {
		super();
	}
	
	public ServerOperationForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ServerOperationForbiddenException(String message) {
		super(message);
	}
	
	public ServerOperationForbiddenException(Throwable cause) {
		super(cause);
	}
}
