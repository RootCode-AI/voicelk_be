package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.Answer;

@Service
public interface AnswerService {

    Answer createAnswer(Answer answer);

    Optional<Answer> getAnswerById(String answerId);

    Optional<Answer> getAnswerByQueryId(String queryId);

    List<Answer> getAllAnswers();

    Answer updateAnswer(String answerId, Answer answer);

    void deleteAnswer(String answerId);
}
