package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.User;

@Service
public interface UserService {

    User createUser(User user);

    Optional<User> getUserById(String userId);

    List<User> getAllUsers();

    User updateUser(String userId, User user);

    void deleteUser(String userId);
}
