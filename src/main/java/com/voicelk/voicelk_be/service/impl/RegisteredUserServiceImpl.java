package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.RegisteredUser;
import com.voicelk.voicelk_be.repository.RegisteredUserRepository;
import com.voicelk.voicelk_be.service.RegisteredUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisteredUserServiceImpl implements RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Override
    public RegisteredUser registerUser(RegisteredUser registeredUser) {
        if (registeredUserRepository.existsByEmail(registeredUser.getEmail())) {
            throw new RuntimeException("Email already in use: " + registeredUser.getEmail());
        }
        registeredUser.setRole("REGISTERED");
        registeredUser.setAccountStatus("ACTIVE");
        return registeredUserRepository.save(registeredUser);
    }

    @Override
    public Optional<RegisteredUser> getRegisteredUserById(String userId) {
        return registeredUserRepository.findById(userId);
    }

    @Override
    public Optional<RegisteredUser> getRegisteredUserByEmail(String email) {
        return registeredUserRepository.findByEmail(email);
    }

    @Override
    public List<RegisteredUser> getAllRegisteredUsers() {
        return registeredUserRepository.findAll();
    }

    @Override
    public List<RegisteredUser> getRegisteredUsersByAccountStatus(String accountStatus) {
        return registeredUserRepository.findByAccountStatus(accountStatus);
    }

    @Override
    public RegisteredUser updateRegisteredUser(String userId, RegisteredUser registeredUser) {
        RegisteredUser existingUser = registeredUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Registered user not found with id: " + userId));

        existingUser.setUserName(registeredUser.getUserName());
        existingUser.setEmail(registeredUser.getEmail());

        if (registeredUser.getPasswordHash() != null && !registeredUser.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(registeredUser.getPasswordHash());
        }

        if (registeredUser.getAccountStatus() != null) {
            existingUser.setAccountStatus(registeredUser.getAccountStatus());
        }

        return registeredUserRepository.save(existingUser);
    }

    @Override
    public void deleteRegisteredUser(String userId) {
        if (!registeredUserRepository.existsById(userId)) {
            throw new RuntimeException("Registered user not found with id: " + userId);
        }
        registeredUserRepository.deleteById(userId);
    }

    @Override
    public boolean isEmailTaken(String email) {
        return registeredUserRepository.existsByEmail(email);
    }
}
