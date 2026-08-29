package com.voicelk.voicelk_be.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredUserDto {

    private String userId;
    private String role;
    private String userName;
    private String email;
    private String accountStatus;
    private int failedLoginCount;
    private LocalDateTime lockTimestamp;
    private String firebaseUid;
    private String profilePicture;
    private String authProvider;
}
