package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.RegisteredUser;

@Service
public interface RegisteredUserService {

    RegisteredUser registerUser(RegisteredUser registeredUser);

    Optional<RegisteredUser> getRegisteredUserById(String userId);

    Optional<RegisteredUser> getRegisteredUserByEmail(String email);

    List<RegisteredUser> getAllRegisteredUsers();

    List<RegisteredUser> getRegisteredUsersByAccountStatus(String accountStatus);

    RegisteredUser updateRegisteredUser(String userId, RegisteredUser registeredUser);

    void deleteRegisteredUser(String userId);

    boolean isEmailTaken(String email);
}
