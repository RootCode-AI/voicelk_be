package com.voicelk.voicelk_be.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "registered_users")
public class RegisteredUser extends User {

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "account_status", nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
    private String accountStatus;

    @Column(name = "failed_login_count", nullable = false, columnDefinition = "integer default 0")
    private int failedLoginCount = 0;

    @Column(name = "lock_timestamp")
    private LocalDateTime lockTimestamp;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "auth_provider", columnDefinition = "varchar(20) default 'LOCAL'")
    private String authProvider;

}
