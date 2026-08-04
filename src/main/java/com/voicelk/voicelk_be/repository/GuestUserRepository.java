package com.voicelk.voicelk_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.GuestUser;

@Repository
public interface GuestUserRepository extends JpaRepository<GuestUser, String> {

    Optional<GuestUser> findBySessionId(String sessionId);

    Optional<GuestUser> findByIpAddress(String ipAddress);
}
