package edu.cbsystematics.com.libraryprojectcbs.config.dateValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class AgeValidator implements ConstraintValidator<AgeConstraint, LocalDate> {

    private int minAge;

    @Override
    public void initialize(AgeConstraint constraint) {
        this.minAge = constraint.minAge();
    }

    @Override
    public boolean isValid(LocalDate birthDate, ConstraintValidatorContext context) {
        if (birthDate == null) {
            return false; // Handling the case where the date of birth is null
        }

        LocalDate currentDate = LocalDate.now();
        long years = ChronoUnit.YEARS.between(birthDate, currentDate);

        return years >= minAge;
    }

}