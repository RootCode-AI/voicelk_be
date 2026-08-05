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

@RestController
@RequestMapping("/api/ask")
@CrossOrigin(origins = "*")
public class QueryAnswerController {

    @Autowired
    private QueryAnswerService queryAnswerService;

    /**
     * Submit a query — saves to DB, generates answer via Gemini, saves answer, returns both.
     */
    @PostMapping
    public ResponseEntity<QueryResponse> submitQuery(@RequestBody QueryRequest queryRequest) {
        QueryResponse response = queryAnswerService.submitQuery(queryRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a specific query with its answer.
     */
    @GetMapping("/{queryId}")
    public ResponseEntity<QueryResponse> getQueryWithAnswer(@PathVariable String queryId) {
        QueryResponse response = queryAnswerService.getQueryWithAnswer(queryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all queries with answers for a user (most recent first).
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<QueryResponse>> getQueryHistory(@PathVariable String userId) {
        List<QueryResponse> history = queryAnswerService.getQueryHistoryByUserId(userId);
        return ResponseEntity.ok(history);
    }
}
