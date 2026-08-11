package com.voicelk.voicelk_be.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.entity.GuestUser;
import com.voicelk.voicelk_be.service.GuestUserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuestUserController {

    private final GuestUserService guestUserService;

    @PostMapping
    public ResponseEntity<GuestUser> createGuestUser(@RequestBody GuestUser guestUser,
            HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        GuestUser createdUser = guestUserService.createGuestUser(guestUser, ipAddress);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GuestUser> getGuestUserById(@PathVariable String userId) {
        return guestUserService.getGuestUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<GuestUser> getGuestUserBySessionId(@PathVariable String sessionId) {
        return guestUserService.getGuestUserBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<GuestUser>> getAllGuestUsers() {
        List<GuestUser> guestUsers = guestUserService.getAllGuestUsers();
        return ResponseEntity.ok(guestUsers);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<GuestUser> updateGuestUser(@PathVariable String userId,
            @RequestBody GuestUser guestUser) {
        GuestUser updatedUser = guestUserService.updateGuestUser(userId, guestUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteGuestUser(@PathVariable String userId) {
        guestUserService.deleteGuestUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the real client IP address from the request.
     * Checks proxy headers first (X-Forwarded-For, Proxy-Client-IP, WL-Proxy-Client-IP),
     * then falls back to the direct remote address.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // 1. Check if the request passed through a Load Balancer or Proxy (like API Gateway)
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        // 2. If no proxies were used, get the direct IP address
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // 3. If multiple IPs are returned by a proxy, the first one is the true client
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}
