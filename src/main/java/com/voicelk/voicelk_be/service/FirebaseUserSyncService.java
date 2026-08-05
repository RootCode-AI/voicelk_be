package com.voicelk.voicelk_be.service;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.voicelk.voicelk_be.entity.RegisteredUser;

@Service
public interface FirebaseUserSyncService {

    /**
     * Syncs a Firebase-authenticated user to the local database.
     * If the user already exists (by email), updates their profile.
     * If not, creates a new RegisteredUser.
     *
     * @param firebaseToken the decoded Firebase token
     * @return the synced RegisteredUser entity
     */
    RegisteredUser syncFirebaseUser(FirebaseToken firebaseToken);
}
