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

import com.voicelk.voicelk_be.entity.GuestUser;
import com.voicelk.voicelk_be.service.GuestUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuestUserController {

    private final GuestUserService guestUserService;

    @PostMapping
    public ResponseEntity<GuestUser> createGuestUser(@RequestBody GuestUser guestUser) {
        GuestUser createdUser = guestUserService.createGuestUser(guestUser);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GuestUser> getGuestUserById(@PathVariable String userId) {
        return guestUserService.getGuestUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<GuestUser> getGuestUserBySessionId(@PathVariable String sessionId) {
        return guestUserService.getGuestUserBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<GuestUser>> getAllGuestUsers() {
        List<GuestUser> guestUsers = guestUserService.getAllGuestUsers();
        return ResponseEntity.ok(guestUsers);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<GuestUser> updateGuestUser(@PathVariable String userId,
            @RequestBody GuestUser guestUser) {
        GuestUser updatedUser = guestUserService.updateGuestUser(userId, guestUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteGuestUser(@PathVariable String userId) {
        guestUserService.deleteGuestUser(userId);
        return ResponseEntity.noContent().build();
    }
}
