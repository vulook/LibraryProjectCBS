package edu.cbsystematics.com.libraryprojectcbs.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;


public record CustomUserPrincipal(User user) implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRole() != null ?
                Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().getRoleName())) : Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    // "User account is enabled / disabled
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }


}