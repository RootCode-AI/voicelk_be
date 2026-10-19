package com.voicelk.voicelk_be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.dto.GuestUserDto;
import com.voicelk.voicelk_be.entity.GuestUser;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.service.GuestUserService;
import com.voicelk.voicelk_be.util.ClientIp;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestUserController {

    private final GuestUserService guestUserService;
    private final EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<GuestUserDto> createGuestUser(@RequestBody GuestUser guestUser,
            HttpServletRequest request) {
        String ipAddress = ClientIp.of(request);
        GuestUser createdUser = guestUserService.createGuestUser(guestUser, ipAddress);
        return new ResponseEntity<>(entityMapper.toDto(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GuestUserDto> getGuestUserById(@PathVariable String userId) {
        return guestUserService.getGuestUserById(userId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<GuestUserDto> getGuestUserBySessionId(@PathVariable String sessionId) {
        return guestUserService.getGuestUserBySessionId(sessionId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<GuestUserDto>> getAllGuestUsers() {
        List<GuestUser> guestUsers = guestUserService.getAllGuestUsers();
        return ResponseEntity.ok(entityMapper.toGuestUserDtoList(guestUsers));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<GuestUserDto> updateGuestUser(@PathVariable String userId,
            @RequestBody GuestUser guestUser) {
        GuestUser updatedUser = guestUserService.updateGuestUser(userId, guestUser);
        return ResponseEntity.ok(entityMapper.toDto(updatedUser));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteGuestUser(@PathVariable String userId) {
        guestUserService.deleteGuestUser(userId);
        return ResponseEntity.noContent().build();
    }
}
