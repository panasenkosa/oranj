package org.oranj.exceptions;

public class OranjException extends Exception{

	public OranjException() {
		
	}	
	
	public OranjException(String message, Throwable e) {
		super(message, e);		
	}
	
	public OranjException(Throwable e) {
		super(e);		
	}	

	public OranjException(String message) {
		super(message);
	}	
}
