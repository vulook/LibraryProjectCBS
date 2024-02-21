package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Component
public class CustomLogoutLogger {

    private final LogsService logsService;
    private final UserService userService;

    @Autowired
    public CustomLogoutLogger(@Lazy LogsService logsService, @Lazy UserService userService) {
        this.logsService = logsService;
        this.userService = userService;
    }

    public void logLogout(Authentication authentication) {
        // Check if authentication is not null and is an instance of UserDetails
        Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast)
                .map(u -> userService.findByEmail(u.getUsername()))
                .ifPresent(user -> {
                    // Extract user information
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    String role = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(""));

                    // Save logout log
                    logsService.saveLog(fullName, role, ActionType.LOGOUT, "logoutSuccessHandler", "[logged out successfully]", (long) (new Random().nextInt(4) + 1), user);
                });
    }

}