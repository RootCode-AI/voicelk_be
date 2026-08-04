package com.voicelk.voicelk_be.security.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.RegisteredUser;
import com.voicelk.voicelk_be.repository.RegisteredUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private RegisteredUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        RegisteredUser registeredUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new User(
                registeredUser.getEmail(),
                registeredUser.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + registeredUser.getRole()))
        );
    }
}
