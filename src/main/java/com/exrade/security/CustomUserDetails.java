package com.exrade.security;

import com.exrade.models.userprofile.Membership;
import com.exrade.models.userprofile.Negotiator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Rhidoy
 * @created 6/27/22
 */
public class CustomUserDetails implements UserDetails {
    private final String password;
    private final Negotiator negotiator;

    public CustomUserDetails(String password, Negotiator negotiator) {
        this.password = password;
        this.negotiator = negotiator;
    }

    public Negotiator getNegotiator() {
        return negotiator;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        if (((Membership) negotiator).getUser().getPlatformRole() != null) {
            list.add(new SimpleGrantedAuthority(((Membership) negotiator).getUser().getPlatformRole().getName()));
        }
        return list;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return negotiator.getUser().getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return negotiator.isActive() ;
    }

    @Override
    public boolean isAccountNonLocked() {
        return negotiator.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return negotiator.isProfileActive();
    }

    @Override
    public boolean isEnabled() {
        return negotiator.isActive();
    }
}
