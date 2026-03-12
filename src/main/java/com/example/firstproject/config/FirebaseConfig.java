package com.example.firstproject.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // For development: Use environment variable or service account JSON
            String serviceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT");
            
            FirebaseOptions options;
            
            if (serviceAccountJson != null && !serviceAccountJson.isEmpty()) {
                // Use service account JSON from environment variable
                InputStream serviceAccount = new ByteArrayInputStream(serviceAccountJson.getBytes());
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            } else {
                // For development: Use application default credentials
                // Make sure to set GOOGLE_APPLICATION_CREDENTIALS environment variable
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .build();
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully");
            }
        } catch (IOException e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
