package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.UserFeedback;

@Service
public interface UserFeedbackService {

    UserFeedback createFeedback(UserFeedback feedback);

    Optional<UserFeedback> getFeedbackById(String feedbackId);

    List<UserFeedback> getFeedbacksByUserId(String userId);

    Optional<UserFeedback> getFeedbackByAudioId(String audioId);

    List<UserFeedback> getAllFeedbacks();

    UserFeedback updateFeedback(String feedbackId, UserFeedback feedback);

    void deleteFeedback(String feedbackId);
}
