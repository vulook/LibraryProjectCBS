package edu.cbsystematics.com.libraryprojectcbs.exception;

public class BookNotFoundException extends RuntimeException {
	
    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
	
}