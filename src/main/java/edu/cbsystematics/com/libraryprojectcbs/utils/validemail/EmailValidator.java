package edu.cbsystematics.com.libraryprojectcbs.utils.validemail;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    // Regular expression pattern for validating email addresses.
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

    // Initialization method called by the validation framework.
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        // TODO document why this method is empty
    }

    // Method to check the validity of the provided email address.
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return (validateEmail(email));
    }

    // Private method to perform the email address validation using the defined regular expression pattern.
    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
