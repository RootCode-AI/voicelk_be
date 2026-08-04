package com.voicelk.voicelk_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "guest_users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GuestUser extends User {

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
}
