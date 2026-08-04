package com.voicelk.voicelk_be.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false, unique = true, length = 50)
    private String userId;

    @Column(name = "role", nullable = false)
    private String role;

    public User() {
    }

    public User(String role) {
        this.role = role;
    }

    /**
     * Automatically generates a custom UUID in the format "vlk_userXXXXXX"
     * before persisting to the database.
     */
    @PrePersist
    private void generateId() {
        if (this.userId == null) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            // Take the first 6 characters for a short, readable ID
            this.userId = "vlk_user" + uuid.substring(0, 6);
        }
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
