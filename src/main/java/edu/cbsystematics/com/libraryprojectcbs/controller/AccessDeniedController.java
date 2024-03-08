package edu.cbsystematics.com.libraryprojectcbs.controller;

import edu.cbsystematics.com.libraryprojectcbs.aop.Loggable;
import edu.cbsystematics.com.libraryprojectcbs.config.security.UserAuthenticationUtils;
import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import edu.cbsystematics.com.libraryprojectcbs.utils.ErrorMessageDetails;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Controller
public class AccessDeniedController {

    private final UserAuthenticationUtils userAuthenticationUtils;

    private final UserService userService;

    @Autowired
    public AccessDeniedController(UserAuthenticationUtils userAuthenticationUtils, UserService userService) {
        this.userAuthenticationUtils = userAuthenticationUtils;
        this.userService = userService;
    }

    @Loggable(ActionType.ACCESS)
    @GetMapping("/access-denied")
    public String accessDenied(HttpSession session, Model model) {

        String username;
        String fullName = null;
        String role;

        // Retrieve the URI from the session attribute
        String uri = (String) session.getAttribute("accessDeniedUri");

        // Remove the session attribute to avoid using it again
        session.removeAttribute("accessDeniedUri");

        // Get the HTTP error status code from the session
        Object status = session.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = status != null ? Integer.parseInt(status.toString()) : HttpStatus.FORBIDDEN.value();

        if (statusCode == HttpStatus.FORBIDDEN.value()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Get the current username and role using userAuthenticationUtils
                username = userAuthenticationUtils.getCurrentUsername(authentication);
                role = userAuthenticationUtils.getCurrentUserRoles(authentication);
                fullName = getFullName(username);
            } else {
                username = "Anonymous";
                role = "ROLE_ANONYMOUS";
            }
        } else {
            username = "Unauthorized";
            role = "ROLE_ANONYMOUS";
        }

        // Build the error message details an object
        ErrorMessageDetails details = ErrorMessageDetails.builder()
                .timestamp(getCurrentDateAndTime())
                .status(statusCode)
                .path(uri)
                .username(username)
                .role(role)
                .error("AccessDeniedException")
                .message("You are not authorized to access this resource")
                .build();

        model.addAttribute("details", details);
        model.addAttribute("fullName", fullName);

        return "error/access-denied";
    }

    // Gets the current date and time formatted as "dd.MM.yyyy HH:mm:ss".
    private String getCurrentDateAndTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return now.format(formatter);
    }

    // Gets the current user
    private String getFullName(String username) {
        User user = userService.findByEmail(username);
        return Optional.ofNullable(user)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("ANONYMOUS");
    }


}