package edu.cbsystematics.com.libraryprojectcbs.config.security;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.service.LogsService;
import edu.cbsystematics.com.libraryprojectcbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Component
public class CustomLoginLogger {

    private final LogsService logsService;
    private final UserService userService;

    @Autowired
    public CustomLoginLogger(@Lazy LogsService logsService, @Lazy UserService userService) {
        this.logsService = logsService;
        this.userService = userService;
    }

    public void logLogin(UserDetails userDetails) {
        // Check if userDetails is not null
        Optional.ofNullable(userDetails)
                .map(UserDetails::getUsername)
                .map(userService::findByEmail)
                .ifPresent(user -> {
                    // Extract user information
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    String role = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(""));

                    // Save login log
                    logsService.saveLog(fullName, role, ActionType.LOGIN, "successHandler", "[logged in successfully]", (long) (new Random().nextInt(4) + 1), user);
                });
    }

}