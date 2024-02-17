package edu.cbsystematics.com.libraryprojectcbs.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class LoginSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final CustomLoginLogger customLoginLogger;

    @Autowired
    public LoginSuccessEventListener(CustomLoginLogger customLoginLogger) {
        this.customLoginLogger = customLoginLogger;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Extract UserDetails object from the Authentication event
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();

        // Log the login event using the logLogin
        customLoginLogger.logLogin(userDetails);
    }

}