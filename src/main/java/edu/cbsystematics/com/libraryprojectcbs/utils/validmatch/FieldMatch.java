package edu.cbsystematics.com.libraryprojectcbs.utils.validmatch;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class) // Indicates the validator class responsible for validation.
@Documented
public @interface FieldMatch {

    // Default error message for constraint violation.
    String message() default "Field values do not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Specifies the first field name to be matched.
    String first();

    // Specifies the second field name to be matched.
    String second();


    // Nested annotation for defining a list of FieldMatch annotations.
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        FieldMatch[] value();
    }

}