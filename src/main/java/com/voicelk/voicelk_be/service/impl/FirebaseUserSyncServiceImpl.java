package com.voicelk.voicelk_be.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.voicelk.voicelk_be.entity.RegisteredUser;
import com.voicelk.voicelk_be.repository.RegisteredUserRepository;
import com.voicelk.voicelk_be.service.FirebaseUserSyncService;

@Service
public class FirebaseUserSyncServiceImpl implements FirebaseUserSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseUserSyncServiceImpl.class);

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Override
    public RegisteredUser syncFirebaseUser(FirebaseToken firebaseToken) {
        String email = firebaseToken.getEmail();
        String uid = firebaseToken.getUid();
        String name = firebaseToken.getName();
        String picture = (String) firebaseToken.getClaims().get("picture");

        Optional<RegisteredUser> existingUser = registeredUserRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Update existing user with latest Firebase info
            RegisteredUser user = existingUser.get();
            if (user.getFirebaseUid() == null) {
                user.setFirebaseUid(uid);
            }
            if (picture != null) {
                user.setProfilePicture(picture);
            }
            if (name != null && !name.isEmpty()) {
                user.setUserName(name);
            }
            LOGGER.info("Synced existing user: {}", email);
            return registeredUserRepository.save(user);
        } else {
            // Create new user from Firebase data
            RegisteredUser newUser = new RegisteredUser();
            newUser.setEmail(email);
            newUser.setUserName(name != null ? name : email);
            newUser.setPasswordHash("FIREBASE_AUTH");
            newUser.setFirebaseUid(uid);
            newUser.setProfilePicture(picture);
            newUser.setAuthProvider("GOOGLE");
            newUser.setRole("REGISTERED");
            newUser.setAccountStatus("ACTIVE");

            LOGGER.info("Created new user from Firebase: {}", email);
            return registeredUserRepository.save(newUser);
        }
    }
}
