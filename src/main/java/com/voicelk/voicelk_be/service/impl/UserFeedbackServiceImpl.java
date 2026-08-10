package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.UserFeedback;
import com.voicelk.voicelk_be.repository.UserFeedbackRepository;
import com.voicelk.voicelk_be.service.UserFeedbackService;

@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {

    @Autowired
    private UserFeedbackRepository userFeedbackRepository;

    @Override
    public UserFeedback createFeedback(UserFeedback feedback) {
        return userFeedbackRepository.save(feedback);
    }

    @Override
    public Optional<UserFeedback> getFeedbackById(String feedbackId) {
        return userFeedbackRepository.findById(feedbackId);
    }

    @Override
    public List<UserFeedback> getFeedbacksByUserId(String userId) {
        return userFeedbackRepository.findByRegisteredUserUserId(userId);
    }

    @Override
    public Optional<UserFeedback> getFeedbackByAudioId(String audioId) {
        return userFeedbackRepository.findByAudioAudioId(audioId);
    }

    @Override
    public List<UserFeedback> getAllFeedbacks() {
        return userFeedbackRepository.findAll();
    }

    @Override
    public UserFeedback updateFeedback(String feedbackId, UserFeedback feedback) {
        UserFeedback existingFeedback = userFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + feedbackId));

        existingFeedback.setRating(feedback.getRating());
        existingFeedback.setComment(feedback.getComment());

        return userFeedbackRepository.save(existingFeedback);
    }

    @Override
    public void deleteFeedback(String feedbackId) {
        if (!userFeedbackRepository.existsById(feedbackId)) {
            throw new RuntimeException("Feedback not found with id: " + feedbackId);
        }
        userFeedbackRepository.deleteById(feedbackId);
    }
}
