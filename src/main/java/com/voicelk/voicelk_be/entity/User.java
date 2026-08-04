package com.voicelk.voicelk_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false, unique = true, length = 50)
    private String userId;

    @Column(name = "role", nullable = false)
    private String role;

    public User(String role) {
        this.role = role;
    }

    @PrePersist
    private void generateId() {
        if (this.userId == null) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            this.userId = "vlk_user" + uuid.substring(0, 6);
        }
    }
}
