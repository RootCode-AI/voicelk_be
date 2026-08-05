package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.dto.QueryRequest;
import com.voicelk.voicelk_be.dto.QueryResponse;
import com.voicelk.voicelk_be.entity.Answer;
import com.voicelk.voicelk_be.entity.Query;
import com.voicelk.voicelk_be.entity.User;
import com.voicelk.voicelk_be.llm.GeminiService;
import com.voicelk.voicelk_be.repository.AnswerRepository;
import com.voicelk.voicelk_be.repository.QueryRepository;
import com.voicelk.voicelk_be.repository.UserRepository;
import com.voicelk.voicelk_be.service.QueryAnswerService;

@Service
public class QueryAnswerServiceImpl implements QueryAnswerService {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeminiService geminiService;

    @Override
    public QueryResponse submitQuery(QueryRequest queryRequest) {
        User user = userRepository.findById(queryRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + queryRequest.getUserId()));

        Query query = new Query();
        query.setInputText(queryRequest.getInputText());
        query.setSyllabusTopic(queryRequest.getSyllabusTopic());
        query.setUser(user);
        query = queryRepository.save(query);

        String generatedText;
        if (queryRequest.getSyllabusTopic() != null && !queryRequest.getSyllabusTopic().isEmpty()) {
            String systemInstruction = "You are an educational assistant. "
                    + "Answer the question related to the topic: " + queryRequest.getSyllabusTopic()
                    + ". Keep the answer concise and within 100 words.";
            generatedText = geminiService.generateAnswer(systemInstruction, queryRequest.getInputText());
        } else {
            generatedText = geminiService.generateAnswer(queryRequest.getInputText());
        }

        Answer answer = new Answer();
        answer.setResponseText(generatedText);
        answer.setSource("Gemini Flash");
        answer.setQuery(query);
        answer = answerRepository.save(answer);

        return mapToResponse(query, answer);
    }

    @Override
    public QueryResponse getQueryWithAnswer(String queryId) {
        Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Query not found with id: " + queryId));

        Answer answer = answerRepository.findByQueryQueryId(queryId).orElse(null);

        return mapToResponse(query, answer);
    }

    @Override
    public List<QueryResponse> getQueryHistoryByUserId(String userId) {
        List<Query> queries = queryRepository.findByUserUserIdOrderByTimestampDesc(userId);

        return queries.stream()
                .map(query -> {
                    Answer answer = answerRepository.findByQueryQueryId(query.getQueryId()).orElse(null);
                    return mapToResponse(query, answer);
                })
                .collect(Collectors.toList());
    }

    private QueryResponse mapToResponse(Query query, Answer answer) {
        QueryResponse response = new QueryResponse();
        response.setQueryId(query.getQueryId());
        response.setInputText(query.getInputText());
        response.setSyllabusTopic(query.getSyllabusTopic());
        response.setTimestamp(query.getTimestamp());
        response.setUserId(query.getUser().getUserId());

        if (answer != null) {
            response.setAnswerId(answer.getAnswerId());
            response.setResponseText(answer.getResponseText());
            response.setSource(answer.getSource());
        }

        return response;
    }
}
