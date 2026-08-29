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

import com.voicelk.voicelk_be.dto.UserFeedbackDto;
import com.voicelk.voicelk_be.entity.UserFeedback;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.service.UserFeedbackService;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
public class UserFeedbackController {

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<UserFeedbackDto> createFeedback(@RequestBody UserFeedback feedback) {
        UserFeedback createdFeedback = userFeedbackService.createFeedback(feedback);
        return new ResponseEntity<>(entityMapper.toDto(createdFeedback), HttpStatus.CREATED);
    }

    @GetMapping("/{feedbackId}")
    public ResponseEntity<UserFeedbackDto> getFeedbackById(@PathVariable String feedbackId) {
        return userFeedbackService.getFeedbackById(feedbackId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFeedbackDto>> getFeedbacksByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(entityMapper.toUserFeedbackDtoList(userFeedbackService.getFeedbacksByUserId(userId)));
    }

    @GetMapping("/audio/{audioId}")
    public ResponseEntity<UserFeedbackDto> getFeedbackByAudioId(@PathVariable String audioId) {
        return userFeedbackService.getFeedbackByAudioId(audioId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserFeedbackDto>> getAllFeedbacks() {
        return ResponseEntity.ok(entityMapper.toUserFeedbackDtoList(userFeedbackService.getAllFeedbacks()));
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<UserFeedbackDto> updateFeedback(@PathVariable String feedbackId, @RequestBody UserFeedback feedback) {
        return ResponseEntity.ok(entityMapper.toDto(userFeedbackService.updateFeedback(feedbackId, feedback)));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable String feedbackId) {
        userFeedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }
}
