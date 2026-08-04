package com.voicelk.voicelk_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.RegisteredUser;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, String> {

    Optional<RegisteredUser> findByEmail(String email);

    Optional<RegisteredUser> findByUserName(String userName);

    boolean existsByEmail(String email);

    List<RegisteredUser> findByAccountStatus(String accountStatus);
}
