package edu.cbsystematics.com.libraryprojectcbs.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final CustomLogoutLogger customLogoutLogger;

    @Autowired
    public CustomLogoutSuccessHandler(CustomLogoutLogger customLogoutLogger) {
        this.customLogoutLogger = customLogoutLogger;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Check if the user was authenticated before logging out
        if (authentication != null) {
            // Extract
            String username = getUsername(authentication);
            String role = getRole(authentication);

            // Print a message
            System.out.print("\033[1;32m");
            System.out.println("User " + username + " logged out with role: " + role);
            System.out.print("\033[0m");

            // Log the logout event using the logLogout
            customLogoutLogger.logLogout(authentication);
        } else {
            System.out.print("User has logged out successfully.");
        }

        // Call to handle the logout success event
        super.onLogoutSuccess(request, response, authentication);
    }

    private String getUsername(Authentication authentication) {
        return authentication.getName();
    }

    private String getRole(Authentication authentication) {
        return authentication.getAuthorities().toString();
    }

}