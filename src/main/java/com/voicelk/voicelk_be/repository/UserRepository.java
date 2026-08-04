package com.voicelk.voicelk_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByRole(String role);
}
