package org.lkdt.common.exception;

public class CloudException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CloudException(String message){
		super(message);
	}
	
	public CloudException(Throwable cause)
	{
		super(cause);
	}
	
	public CloudException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
