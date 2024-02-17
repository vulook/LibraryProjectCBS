package edu.cbsystematics.com.libraryprojectcbs.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ValidationExceptionHandler {

    public static void handleValidationErrors(BindingResult result) {
        List<ObjectError> errors = result.getAllErrors();

        for (ObjectError error : errors) {
            System.out.println(error.getDefaultMessage());
        }
    }
}