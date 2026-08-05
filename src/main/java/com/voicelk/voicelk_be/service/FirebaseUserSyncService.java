package com.voicelk.voicelk_be.service;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.voicelk.voicelk_be.entity.RegisteredUser;

@Service
public interface FirebaseUserSyncService {

    RegisteredUser syncFirebaseUser(FirebaseToken firebaseToken);
}
