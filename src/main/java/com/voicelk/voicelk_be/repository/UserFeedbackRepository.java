package com.voicelk.voicelk_be.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.UserFeedback;

@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, String> {

    List<UserFeedback> findByRegisteredUserUserId(String userId);

    Optional<UserFeedback> findByAudioAudioId(String audioId);
}
