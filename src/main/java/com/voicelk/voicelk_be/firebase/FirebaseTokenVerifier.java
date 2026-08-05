package com.voicelk.voicelk_be.firebase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Service
public class FirebaseTokenVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseTokenVerifier.class);

    @Autowired
    private FirebaseAuth firebaseAuth;

    /**
     * Verifies a Firebase ID token and returns the decoded token.
     *
     * @param idToken the Firebase ID token from the frontend
     * @return FirebaseToken containing uid, email, name, picture
     * @throws FirebaseAuthException if the token is invalid or expired
     */
    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        LOGGER.info("Firebase token verified for user: {}", decodedToken.getEmail());
        return decodedToken;
    }
}
