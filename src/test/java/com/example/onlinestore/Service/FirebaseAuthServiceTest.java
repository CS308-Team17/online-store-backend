package com.example.onlinestore.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FirebaseAuthServiceTest {

    private FirebaseAuthService firebaseAuthService;

    @BeforeEach
    void setUp() {
        firebaseAuthService = new FirebaseAuthService();
    }

    @Test
    void verifyIdToken_ShouldReturnFirebaseToken_WhenValidToken() throws FirebaseAuthException {
        // Arrange
        String idToken = "valid_id_token";
        FirebaseToken mockToken = mock(FirebaseToken.class);

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockAuth = mock(FirebaseAuth.class);
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
            when(mockAuth.verifyIdToken(idToken)).thenReturn(mockToken);

            // Act
            FirebaseToken result = firebaseAuthService.verifyIdToken(idToken);

            // Assert
            assertEquals(mockToken, result);
            verify(mockAuth).verifyIdToken(idToken);
        }
    }

    @Test
    void verifyIdToken_ShouldThrowException_WhenInvalidToken() throws FirebaseAuthException {
        // Arrange
        String idToken = "invalid_id_token";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockAuth = mock(FirebaseAuth.class);
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(mockAuth);

            FirebaseAuthException exceptionMock = mock(FirebaseAuthException.class);
            when(mockAuth.verifyIdToken(idToken)).thenThrow(exceptionMock);

            // Act & Assert
            FirebaseAuthException exception = assertThrows(FirebaseAuthException.class, () -> {
                firebaseAuthService.verifyIdToken(idToken);
            });

            assertEquals(exceptionMock, exception);
            verify(mockAuth).verifyIdToken(idToken);
        }
    }

    @Test
    void createCustomToken_ShouldReturnToken_WhenValidUid() throws FirebaseAuthException {
        // Arrange
        String uid = "valid_uid";
        String expectedToken = "custom_token";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockAuth = mock(FirebaseAuth.class);
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
            when(mockAuth.createCustomToken(uid)).thenReturn(expectedToken);

            // Act
            String result = firebaseAuthService.createCustomToken(uid);

            // Assert
            assertEquals(expectedToken, result);
            verify(mockAuth).createCustomToken(uid);
        }
    }

    @Test
    void createCustomToken_ShouldThrowException_WhenInvalidUid() throws FirebaseAuthException {
        // Arrange
        String uid = "invalid_uid";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockAuth = mock(FirebaseAuth.class);
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(mockAuth);

            FirebaseAuthException exceptionMock = mock(FirebaseAuthException.class);
            when(mockAuth.createCustomToken(uid)).thenThrow(exceptionMock);

            // Act & Assert
            FirebaseAuthException exception = assertThrows(FirebaseAuthException.class, () -> {
                firebaseAuthService.createCustomToken(uid);
            });

            assertEquals(exceptionMock, exception);
            verify(mockAuth).createCustomToken(uid);
        }
    }
}
