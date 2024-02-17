package edu.cbsystematics.com.libraryprojectcbs.exception;


public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String message) {
        super(message);
    }

    public BookAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}