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

import com.voicelk.voicelk_be.dto.AnswerDto;
import com.voicelk.voicelk_be.entity.Answer;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.service.AnswerService;

@RestController
@RequestMapping("/api/answers")
@CrossOrigin(origins = "*")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<AnswerDto> createAnswer(@RequestBody Answer answer) {
        Answer createdAnswer = answerService.createAnswer(answer);
        return new ResponseEntity<>(entityMapper.toDto(createdAnswer), HttpStatus.CREATED);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<AnswerDto> getAnswerById(@PathVariable String answerId) {
        return answerService.getAnswerById(answerId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/query/{queryId}")
    public ResponseEntity<AnswerDto> getAnswerByQueryId(@PathVariable String queryId) {
        return answerService.getAnswerByQueryId(queryId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AnswerDto>> getAllAnswers() {
        return ResponseEntity.ok(entityMapper.toAnswerDtoList(answerService.getAllAnswers()));
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<AnswerDto> updateAnswer(@PathVariable String answerId, @RequestBody Answer answer) {
        return ResponseEntity.ok(entityMapper.toDto(answerService.updateAnswer(answerId, answer)));
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable String answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
}
