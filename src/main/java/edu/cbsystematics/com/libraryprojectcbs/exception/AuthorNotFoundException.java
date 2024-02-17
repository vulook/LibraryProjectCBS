package edu.cbsystematics.com.libraryprojectcbs.exception;

public class AuthorNotFoundException extends RuntimeException {
	
    public AuthorNotFoundException(String message) {
        super(message);
    }

    public AuthorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
	
}