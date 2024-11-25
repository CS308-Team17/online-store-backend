package com.example.onlinestore.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String role;

    public FirebaseAuthenticationToken(Object principal, String role) {
        super(Collections.singletonList(new SimpleGrantedAuthority(role)));
        this.principal = principal;
        this.role = role;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;  // Not used
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getRole() {
        return role;
    }
}
