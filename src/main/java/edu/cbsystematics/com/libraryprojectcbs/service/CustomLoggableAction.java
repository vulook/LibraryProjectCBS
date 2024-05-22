package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.ActionType;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import org.aspectj.lang.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
public class CustomLoggableAction {

    private final LogsService logsService;
    private final UserService userService;

    @Autowired
    public CustomLoggableAction(@Lazy LogsService logsService, @Lazy UserService userService) {
        this.logsService = logsService;
        this.userService = userService;
    }

    public void logLoggableAction(String email, String role, ActionType actionType, Signature methodSignature, Object[] methodArguments, long executionTime) {
        // Find the user by email
        User user = userService.findByEmail(email).orElse(null);

        // Extract user information
        String fullName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "ANONYMOUS";

        // Save the log Action
        logsService.saveLog(fullName, role, actionType, methodSignature.getName(), Arrays.toString(methodArguments), executionTime, user);
    }

}