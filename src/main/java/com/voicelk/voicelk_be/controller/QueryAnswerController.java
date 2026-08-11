package com.voicelk.voicelk_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.dto.QueryRequest;
import com.voicelk.voicelk_be.dto.QueryResponse;
import com.voicelk.voicelk_be.service.QueryAnswerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ask")
@CrossOrigin(origins = "*")
public class QueryAnswerController {

    @Autowired
    private QueryAnswerService queryAnswerService;

    @PostMapping
    public ResponseEntity<QueryResponse> submitQuery(@RequestBody QueryRequest queryRequest,
            HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        QueryResponse response = queryAnswerService.submitQuery(queryRequest, ipAddress);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{queryId}")
    public ResponseEntity<QueryResponse> getQueryWithAnswer(@PathVariable String queryId) {
        QueryResponse response = queryAnswerService.getQueryWithAnswer(queryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<QueryResponse>> getQueryHistory(@PathVariable String userId) {
        List<QueryResponse> history = queryAnswerService.getQueryHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Extracts the real client IP address from the request.
     * Checks proxy headers first, then falls back to the direct remote address.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }
}
