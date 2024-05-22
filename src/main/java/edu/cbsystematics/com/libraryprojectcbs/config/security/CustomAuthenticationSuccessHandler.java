package edu.cbsystematics.com.libraryprojectcbs.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import static edu.cbsystematics.com.libraryprojectcbs.LibraryProjectCbsApplication.*;


@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Determine the target URL based on the user's role.
        String targetUrl = determineTargetUrl(authentication);
        // Redirect the user to the determined target URL.
        response.sendRedirect(targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {

        String authority = authentication.getAuthorities().stream().findFirst().orElseThrow().getAuthority();

        // Redirect to different URLs based on the user's role
        return switch (authority) {
            case ROLE_ADMIN -> ADMIN_HOME_URL;              // Redirect to admin home page for admins
            case ROLE_LIBRARIAN -> LIBRARIAN_HOME_URL;      // Redirect to librarian home page for librarians
            case ROLE_READER -> READER_HOME_URL;            // Redirect to reader home page for readers
            case ROLE_WORKER -> WORKER_HOME_URL;            // Redirect to worker home page
            default ->
                    throw new IllegalStateException("Unexpected role: " + authority);
        };
    }

}

