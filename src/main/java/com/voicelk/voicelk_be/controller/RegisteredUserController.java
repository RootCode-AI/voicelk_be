package com.voicelk.voicelk_be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.entity.RegisteredUser;
import com.voicelk.voicelk_be.service.RegisteredUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reg")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegisteredUserController {

    private final RegisteredUserService registeredUserService;

    @PostMapping
    public ResponseEntity<RegisteredUser> registerUser(@RequestBody RegisteredUser registeredUser) {
        RegisteredUser createdUser = registeredUserService.registerUser(registeredUser);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RegisteredUser> getRegisteredUserById(@PathVariable String userId) {
        return registeredUserService.getRegisteredUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<RegisteredUser> getRegisteredUserByEmail(@PathVariable String email) {
        return registeredUserService.getRegisteredUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RegisteredUser>> getAllRegisteredUsers() {
        List<RegisteredUser> users = registeredUserService.getAllRegisteredUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/status/{accountStatus}")
    public ResponseEntity<List<RegisteredUser>> getRegisteredUsersByAccountStatus(
            @PathVariable String accountStatus) {
        List<RegisteredUser> users = registeredUserService.getRegisteredUsersByAccountStatus(accountStatus);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RegisteredUser> updateRegisteredUser(@PathVariable String userId,
            @RequestBody RegisteredUser registeredUser) {
        RegisteredUser updatedUser = registeredUserService.updateRegisteredUser(userId, registeredUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteRegisteredUser(@PathVariable String userId) {
        registeredUserService.deleteRegisteredUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> isEmailTaken(@PathVariable String email) {
        return ResponseEntity.ok(registeredUserService.isEmailTaken(email));
    }
}
