package edu.cbsystematics.com.libraryprojectcbs.exception;

public class VerificationCodeCreationException extends RuntimeException {

    public VerificationCodeCreationException(String message) {
        super(message);
    }

    public VerificationCodeCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}