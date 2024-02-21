package edu.cbsystematics.com.libraryprojectcbs.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class CustomUserDetails extends User implements UserDetails {

    public CustomUserDetails(User user) {
        super(user.getEmail(), user.getPassword(), user.getUserRole());
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return super.getUserRole() != null ?
                Collections.singletonList(new SimpleGrantedAuthority(super.getUserRole().getRoleName())) :
                Collections.emptyList();
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

    @Override
    public boolean isEnabled() {
        return true;
    }


}