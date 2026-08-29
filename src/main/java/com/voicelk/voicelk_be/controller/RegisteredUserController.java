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

import com.voicelk.voicelk_be.dto.RegisteredUserDto;
import com.voicelk.voicelk_be.entity.RegisteredUser;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.service.RegisteredUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reg")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegisteredUserController {

    private final RegisteredUserService registeredUserService;
    private final EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<RegisteredUserDto> registerUser(@RequestBody RegisteredUser registeredUser) {
        RegisteredUser createdUser = registeredUserService.registerUser(registeredUser);
        return new ResponseEntity<>(entityMapper.toDto(createdUser), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RegisteredUserDto> getRegisteredUserById(@PathVariable String userId) {
        return registeredUserService.getRegisteredUserById(userId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<RegisteredUserDto> getRegisteredUserByEmail(@PathVariable String email) {
        return registeredUserService.getRegisteredUserByEmail(email)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RegisteredUserDto>> getAllRegisteredUsers() {
        List<RegisteredUser> users = registeredUserService.getAllRegisteredUsers();
        return ResponseEntity.ok(entityMapper.toRegisteredUserDtoList(users));
    }

    @GetMapping("/status/{accountStatus}")
    public ResponseEntity<List<RegisteredUserDto>> getRegisteredUsersByAccountStatus(
            @PathVariable String accountStatus) {
        List<RegisteredUser> users = registeredUserService.getRegisteredUsersByAccountStatus(accountStatus);
        return ResponseEntity.ok(entityMapper.toRegisteredUserDtoList(users));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RegisteredUserDto> updateRegisteredUser(@PathVariable String userId,
            @RequestBody RegisteredUser registeredUser) {
        RegisteredUser updatedUser = registeredUserService.updateRegisteredUser(userId, registeredUser);
        return ResponseEntity.ok(entityMapper.toDto(updatedUser));
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
