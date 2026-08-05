package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.Answer;
import com.voicelk.voicelk_be.repository.AnswerRepository;
import com.voicelk.voicelk_be.service.AnswerService;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public Answer createAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public Optional<Answer> getAnswerById(String answerId) {
        return answerRepository.findById(answerId);
    }

    @Override
    public Optional<Answer> getAnswerByQueryId(String queryId) {
        return answerRepository.findByQueryQueryId(queryId);
    }

    @Override
    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    @Override
    public Answer updateAnswer(String answerId, Answer answer) {
        Answer existingAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + answerId));

        existingAnswer.setResponseText(answer.getResponseText());
        existingAnswer.setSource(answer.getSource());

        return answerRepository.save(existingAnswer);
    }

    @Override
    public void deleteAnswer(String answerId) {
        if (!answerRepository.existsById(answerId)) {
            throw new RuntimeException("Answer not found with id: " + answerId);
        }
        answerRepository.deleteById(answerId);
    }
}
