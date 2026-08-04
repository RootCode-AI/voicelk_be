package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.GuestUser;
import com.voicelk.voicelk_be.repository.GuestUserRepository;
import com.voicelk.voicelk_be.service.GuestUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestUserServiceImpl implements GuestUserService {

    @Autowired
    private GuestUserRepository guestUserRepository;

    @Override
    public GuestUser createGuestUser(GuestUser guestUser) {
        guestUser.setRole("GUEST");
        return guestUserRepository.save(guestUser);
    }

    @Override
    public Optional<GuestUser> getGuestUserById(String userId) {
        return guestUserRepository.findById(userId);
    }

    @Override
    public Optional<GuestUser> getGuestUserBySessionId(String sessionId) {
        return guestUserRepository.findBySessionId(sessionId);
    }

    @Override
    public List<GuestUser> getAllGuestUsers() {
        return guestUserRepository.findAll();
    }

    @Override
    public GuestUser updateGuestUser(String userId, GuestUser guestUser) {
        GuestUser existingUser = guestUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Guest user not found with id: " + userId));

        existingUser.setSessionId(guestUser.getSessionId());
        existingUser.setIpAddress(guestUser.getIpAddress());

        return guestUserRepository.save(existingUser);
    }

    @Override
    public void deleteGuestUser(String userId) {
        if (!guestUserRepository.existsById(userId)) {
            throw new RuntimeException("Guest user not found with id: " + userId);
        }
        guestUserRepository.deleteById(userId);
    }
}
