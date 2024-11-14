package com.example.onlinestore.config;

import com.example.onlinestore.constants.FirebaseConstants;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Update this path to point to your service account JSON file
            InputStream serviceAccount = new FileInputStream(FirebaseConstants.SERVICE_ACCOUNT_FILE_PATH);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(FirebaseConstants.STORAGE_BUCKET)
                    .setDatabaseUrl(FirebaseConstants.DATABASE_URL)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
