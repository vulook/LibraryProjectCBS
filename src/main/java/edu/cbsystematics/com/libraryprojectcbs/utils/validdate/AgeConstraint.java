package edu.cbsystematics.com.libraryprojectcbs.utils.validdate;

import jakarta.validation.Payload;
import jakarta.validation.Constraint;
import java.lang.annotation.*;

import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.MIN_AGE;


@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
@Documented
public @interface AgeConstraint {

    int minAge() default MIN_AGE; // Minimum permissible age in years

    String message() default "You must be at least {minAge} years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}