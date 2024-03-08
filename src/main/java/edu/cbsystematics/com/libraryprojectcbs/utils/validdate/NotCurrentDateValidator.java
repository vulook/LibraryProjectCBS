package edu.cbsystematics.com.libraryprojectcbs.utils.validdate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;


public class NotCurrentDateValidator implements ConstraintValidator<ValidData, LocalDate> {

    @Override
    public void initialize(ValidData constraintAnnotation) {
        // TODO document why this method is empty
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now());
    }

}