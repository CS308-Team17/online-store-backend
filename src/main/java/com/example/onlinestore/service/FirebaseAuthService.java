package com.example.onlinestore.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    /**
     * Verifies the Firebase ID token sent from the client.
     *
     * @param idToken The Firebase ID token.
     * @return FirebaseToken object if the token is valid.
     * @throws FirebaseAuthException If the token is invalid or expired.
     */
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }


    /**
     * Generates a Firebase custom token for a given user UID.
     *
     * @param uid The user's UID.
     * @return Firebase custom token.
     * @throws FirebaseAuthException If the UID is invalid.
     */
    public String createCustomToken(String uid) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().createCustomToken(uid);
    }
}
