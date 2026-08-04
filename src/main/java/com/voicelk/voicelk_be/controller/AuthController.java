package com.voicelk.voicelk_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.dto.JwtResponse;
import com.voicelk.voicelk_be.dto.LoginRequest;
import com.voicelk.voicelk_be.dto.MessageResponse;
import com.voicelk.voicelk_be.dto.RegisterRequest;
import com.voicelk.voicelk_be.entity.RegisteredUser;
import com.voicelk.voicelk_be.repository.RegisteredUserRepository;
import com.voicelk.voicelk_be.security.util.JwtUtils;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);

        // Fetch user details from DB for the response
        RegisteredUser user = registeredUserRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                user.getUserId(),
                user.getEmail(),
                user.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {

        // Check if email already exists
        if (registeredUserRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new registered user
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setUserName(registerRequest.getUserName());
        registeredUser.setEmail(registerRequest.getEmail());
        registeredUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        registeredUser.setRole("REGISTERED");
        registeredUser.setAccountStatus("ACTIVE");

        registeredUserRepository.save(registeredUser);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
