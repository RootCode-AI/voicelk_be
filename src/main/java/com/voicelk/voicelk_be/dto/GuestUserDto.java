package com.voicelk.voicelk_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestUserDto {

    private String userId;
    private String role;
    private String sessionId;
    private String ipAddress;
}
