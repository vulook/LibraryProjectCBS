package edu.cbsystematics.com.libraryprojectcbs.utils.validdate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotCurrentDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidData {

    String message() default "The date cannot be greater than the current date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}