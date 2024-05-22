package edu.cbsystematics.com.libraryprojectcbs.config.security;

import edu.cbsystematics.com.libraryprojectcbs.exception.DisabledException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws ServletException, IOException {

        // Default message for invalid email or password
        String errorMessage = "Invalid Email or Password";

        // Handling specific exceptions and customizing the message
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "Username not found: \n" + exception.getMessage();
            log.error(exception.getMessage());
        } else if (exception instanceof AccountExpiredException) {
            errorMessage = "Account expired: \n" + exception.getMessage();
            log.error(exception.getMessage());
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "Credentials expired: \n" + exception.getMessage();
            log.error(exception.getMessage());
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Authentication error: \n" + exception.getMessage();
            log.error(exception.getMessage());
        } else if (exception instanceof DisabledException || exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "Login error: \n" + exception.getMessage();
            log.error(exception.getMessage());
        } else if (exception instanceof LockedException) {
            errorMessage = "Account locked: \n" + exception.getMessage();
            log.error(exception.getMessage());
        }

        // Set errorMessage in session attribute
        request.getSession().setAttribute("err", errorMessage);

        // Redirect to the login page with an error message
        String redirectUrl = response.encodeURL("/login?error");
        setDefaultFailureUrl(redirectUrl);

        super.onAuthenticationFailure(request, response, exception);
    }

}