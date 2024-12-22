package com.example.onlinestore.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FirebaseConfigTest {

    private FirebaseConfig firebaseConfig;

    @BeforeEach
    void setUp() {
        firebaseConfig = new FirebaseConfig();
    }

    @Test
    void initialize_ShouldInitializeFirebaseApp_WhenNotInitialized() throws Exception {
        // Arrange
        String serviceAccountJson = "{}"; // Mock empty JSON for service account
        InputStream mockServiceAccountStream = new ByteArrayInputStream(serviceAccountJson.getBytes());

        try (
                MockedStatic<FirebaseApp> mockedFirebaseApp = mockStatic(FirebaseApp.class);
                MockedStatic<FirebaseOptions> mockedFirebaseOptions = mockStatic(FirebaseOptions.class);
                MockedStatic<GoogleCredentials> mockedGoogleCredentials = mockStatic(GoogleCredentials.class)
        ) {
            GoogleCredentials mockCredentials = mock(GoogleCredentials.class);

            // Mock GoogleCredentials.fromStream
            mockedGoogleCredentials.when(() -> GoogleCredentials.fromStream(any(InputStream.class)))
                    .thenReturn(mockCredentials);

            // Mock FirebaseOptions.Builder
            FirebaseOptions.Builder mockBuilder = mock(FirebaseOptions.Builder.class);
            when(mockBuilder.setCredentials(mockCredentials)).thenReturn(mockBuilder);
            when(mockBuilder.setStorageBucket(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.setDatabaseUrl(anyString())).thenReturn(mockBuilder);
            FirebaseOptions mockOptions = mock(FirebaseOptions.class);
            when(mockBuilder.build()).thenReturn(mockOptions);

            mockedFirebaseOptions.when(FirebaseOptions::builder).thenReturn(mockBuilder);

            // Mock FirebaseApp.getApps to return an empty list
            mockedFirebaseApp.when(FirebaseApp::getApps).thenReturn(List.of());

            // Act
            firebaseConfig.initialize();

            // Assert
            mockedFirebaseApp.verify(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)), times(1));
        }
    }

    @Test
    void initialize_ShouldNotInitializeFirebaseApp_WhenAlreadyInitialized() {
        // Arrange
        try (MockedStatic<FirebaseApp> mockedFirebaseApp = mockStatic(FirebaseApp.class)) {
            mockedFirebaseApp.when(FirebaseApp::getApps).thenReturn(List.of(mock(FirebaseApp.class)));

            // Act
            firebaseConfig.initialize();

            // Assert
            mockedFirebaseApp.verify(() -> FirebaseApp.initializeApp(any(FirebaseOptions.class)), never());
        }
    }


}
