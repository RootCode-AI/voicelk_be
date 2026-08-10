package com.voicelk.voicelk_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.voicelk.voicelk_be.entity.UserFeedback;
import com.voicelk.voicelk_be.service.UserFeedbackService;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
public class UserFeedbackController {

    @Autowired
    private UserFeedbackService userFeedbackService;

    @PostMapping
    public ResponseEntity<UserFeedback> createFeedback(@RequestBody UserFeedback feedback) {
        UserFeedback createdFeedback = userFeedbackService.createFeedback(feedback);
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }

    @GetMapping("/{feedbackId}")
    public ResponseEntity<UserFeedback> getFeedbackById(@PathVariable String feedbackId) {
        return userFeedbackService.getFeedbackById(feedbackId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFeedback>> getFeedbacksByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(userFeedbackService.getFeedbacksByUserId(userId));
    }

    @GetMapping("/audio/{audioId}")
    public ResponseEntity<UserFeedback> getFeedbackByAudioId(@PathVariable String audioId) {
        return userFeedbackService.getFeedbackByAudioId(audioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserFeedback>> getAllFeedbacks() {
        return ResponseEntity.ok(userFeedbackService.getAllFeedbacks());
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<UserFeedback> updateFeedback(@PathVariable String feedbackId, @RequestBody UserFeedback feedback) {
        return ResponseEntity.ok(userFeedbackService.updateFeedback(feedbackId, feedback));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable String feedbackId) {
        userFeedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }
}
