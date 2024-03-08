package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.utils.ErrorMessageDetails;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@ControllerAdvice
public class ExceptionHandlerController {

    private final UserAuthenticationUtils userAuthenticationUtils;

    @Autowired
    public ExceptionHandlerController(UserAuthenticationUtils userAuthenticationUtils) {
        this.userAuthenticationUtils = userAuthenticationUtils;
    }


    @Loggable(ActionType.ACCESS)
    @ExceptionHandler(Exception.class)
    public String globalExceptionHandler(HttpServletRequest request, Exception ex, Model model) {

        String username;
        String role;

        // Get the HTTP error status code from the request
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        // Parse the status code
        int statusCode = status != null ? Integer.parseInt(status.toString()) : HttpStatus.NOT_FOUND.value();

        // Handle unauthorized access
        if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
            username = "Unauthorized";
            role = "ROLE_ANONYMOUS";

        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Get the current username and role using userAuthenticationUtils
                username = userAuthenticationUtils.getCurrentUsername(authentication);
                role = userAuthenticationUtils.getCurrentUserRoles(authentication);

            } else {
                // Set default values
                username = "Anonymous";
                role = "ROLE_ANONYMOUS";
            }
        }

        // Build the error message details an object
        ErrorMessageDetails details = ErrorMessageDetails.builder()
                .timestamp(getCurrentDateAndTime())
                .status(statusCode)
                .path(request.getRequestURI())
                .username(username != null ? username : "Anonymous")
                .role(role)
                .error(ex.getMessage())
                .message("Requested resource wasn't found on the server")
                .build();

        model.addAttribute("details", details);

        return "error/404";
    }


    // Gets the current date and time formatted as "dd.MM.yyyy HH:mm:ss".
    private String getCurrentDateAndTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return now.format(formatter);
    }

}

