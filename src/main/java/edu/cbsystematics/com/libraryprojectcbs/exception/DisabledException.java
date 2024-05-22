package edu.cbsystematics.com.libraryprojectcbs.exception;

import org.springframework.security.authentication.AccountStatusException;

public class DisabledException extends AccountStatusException {
    public DisabledException(String message) {
        super(message);
    }

    public DisabledException(String message, Throwable cause) {
        super(message, cause);
    }

}