package edu.cbsystematics.com.libraryprojectcbs.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class UserAuthenticationUtils {

    // Retrieves the username of the current authenticated user from the Authentication object.
    public final String getCurrentUsername(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast)
                .map(UserDetails::getUsername)
                .orElse(null);
    }

    // Retrieves the role of the current authenticated user from the Authentication object.
    public final String getCurrentUserRoles(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(Authentication::getAuthorities)
                .map(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining("")))
                .orElse(null);
    }

}