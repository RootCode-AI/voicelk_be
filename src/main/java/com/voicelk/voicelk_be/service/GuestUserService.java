package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.GuestUser;

@Service
public interface GuestUserService {

    GuestUser createGuestUser(GuestUser guestUser, String ipAddress); 

    Optional<GuestUser> getGuestUserById(String userId);

    Optional<GuestUser> getGuestUserBySessionId(String sessionId);

    List<GuestUser> getAllGuestUsers();

    GuestUser updateGuestUser(String userId, GuestUser guestUser);

    void deleteGuestUser(String userId);
}
