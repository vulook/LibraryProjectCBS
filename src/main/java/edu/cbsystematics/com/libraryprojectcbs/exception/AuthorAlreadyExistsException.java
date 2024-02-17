package edu.cbsystematics.com.libraryprojectcbs.exception;


public class AuthorAlreadyExistsException extends RuntimeException {

    public AuthorAlreadyExistsException(String message) {
        super(message);
    }

    public AuthorAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}