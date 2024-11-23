package com.example.onlinestore.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String userId;
    private final String token;

    public JwtAuthenticationToken(String userId, String token) {
        super(null); // No authorities provided
        this.userId = userId;
        this.token = token;
        setAuthenticated(true); // Mark this token as authenticated
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
