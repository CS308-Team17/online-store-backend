package com.example.onlinestore.security;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final FirebaseToken firebaseToken;
    private final Object principal;

    public FirebaseAuthenticationToken(FirebaseToken firebaseToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.firebaseToken = firebaseToken;
        this.principal = firebaseToken.getUid(); // Use Firebase UID as the principal
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;  // Firebase token itself is the credential
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public FirebaseToken getFirebaseToken() {
        return this.firebaseToken;
    }
}
