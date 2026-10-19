package com.voicelk.voicelk_be.firebase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;

@Configuration
public class FirebaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials-json:}")
    private String firebaseCredentialsJson;

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp(ResourceLoader resourceLoader) throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            GoogleCredentials credentials;
            try (InputStream serviceAccount = openServiceAccount(resourceLoader)) {
                credentials = GoogleCredentials.fromStream(serviceAccount);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setHttpTransport(new ApacheHttpTransport())
                    .build();

            LOGGER.info("Firebase initialized successfully");
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    /**
     * The service account key. In deployment it comes from a secret (the JSON itself, raw or
     * base64) because the key file is never committed and so is not in a built image;
     * locally it is read from {@code firebase.config.path}.
     */
    private InputStream openServiceAccount(ResourceLoader resourceLoader) throws IOException {
        if (StringUtils.hasText(firebaseCredentialsJson)) {
            String value = firebaseCredentialsJson.trim();
            byte[] json = value.startsWith("{")
                    ? value.getBytes(StandardCharsets.UTF_8)
                    : Base64.getMimeDecoder().decode(value);
            LOGGER.info("Firebase credentials read from FIREBASE_CREDENTIALS_JSON");
            return new ByteArrayInputStream(json);
        }

        Resource resource = resourceLoader.getResource(firebaseConfigPath);
        if (!resource.exists()) {
            throw new IllegalStateException("Firebase service account not found at " + firebaseConfigPath
                    + "; set FIREBASE_CREDENTIALS_JSON or FIREBASE_CONFIG_PATH");
        }
        return resource.getInputStream();
    }
}
