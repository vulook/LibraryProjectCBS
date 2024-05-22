package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.exception.DisabledException;
import edu.cbsystematics.com.libraryprojectcbs.models.User;
import edu.cbsystematics.com.libraryprojectcbs.models.CustomUserPrincipal;
import edu.cbsystematics.com.libraryprojectcbs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // Loads user details
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        // Find username (email address)
        User user = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("The User does not exist, email: %s", username)));

        if (!user.isEnabled()) {
            throw new DisabledException(String.format("The User account (%s) is disabled", user.getEmail()));
        }

        return new CustomUserPrincipal(user);
    }

}

