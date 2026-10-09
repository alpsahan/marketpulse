package com.alpsahan.exception;

public class EmailAlreadyExistsException extends RuntimeException {
	
	public EmailAlreadyExistsException() {
		super("Email already exists");
	}

}
