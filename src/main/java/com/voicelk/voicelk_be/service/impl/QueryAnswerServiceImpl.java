package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.dto.QueryRequest;
import com.voicelk.voicelk_be.dto.QueryResponse;
import com.voicelk.voicelk_be.entity.Answer;
import com.voicelk.voicelk_be.entity.Audio;
import com.voicelk.voicelk_be.entity.GuestUser;
import com.voicelk.voicelk_be.entity.Query;
import com.voicelk.voicelk_be.entity.User;
import com.voicelk.voicelk_be.llm.GeminiService;
import com.voicelk.voicelk_be.repository.AnswerRepository;
import com.voicelk.voicelk_be.repository.AudioRepository;
import com.voicelk.voicelk_be.repository.GuestUserRepository;
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
    private AudioRepository audioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestUserRepository guestUserRepository;

    @Autowired
    private GeminiService geminiService;

    @Override
    public QueryResponse submitQuery(QueryRequest queryRequest, String ipAddress) {
        User user;

        if (queryRequest.getUserId() != null && !queryRequest.getUserId().isEmpty()) {
            // Registered user — look up by userId
            user = userRepository.findById(queryRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + queryRequest.getUserId()));
        } else {
            // Guest user — find existing GuestUser by IP or create a new one
            user = guestUserRepository.findByIpAddress(ipAddress)
                    .orElseGet(() -> {
                        GuestUser guestUser = new GuestUser();
                        guestUser.setRole("GUEST");
                        guestUser.setIpAddress(ipAddress);
                        guestUser.setSessionId(queryRequest.getSessionId() != null
                                ? queryRequest.getSessionId()
                                : UUID.randomUUID().toString());
                        return guestUserRepository.save(guestUser);
                    });
        }

        Query query = new Query();
        query.setInputText(queryRequest.getInputText());
        query.setSyllabusTopic(queryRequest.getSyllabusTopic());
        query.setUser(user);
        query = queryRepository.save(query);

        String systemInstruction = "You are an educational assistant for Sri Lankan O/L and A/L students. "
                + "IMPORTANT RULES:\n"
                + "- STRICT CONSTRAINT: Your response MUST be less than or equal to 100 words. If the explanation is naturally longer, you MUST summarize it to fit within 100 words. Do not exceed this limit under any circumstances.\n"
                + "- The main content must be in Sinhala language.\n"
                + "- You must mix Sinhala words with English technical terms where appropriate.\n"
                + "- The explanation must be simple and easy to understand for O/L and A/L students.\n";

        String generatedText;
        if (queryRequest.getSyllabusTopic() != null && !queryRequest.getSyllabusTopic().isEmpty()) {
            systemInstruction += "Answer the question related to the topic: " + queryRequest.getSyllabusTopic() + ".";
            generatedText = geminiService.generateAnswer(systemInstruction, queryRequest.getInputText());
        } else {
            generatedText = geminiService.generateAnswer(systemInstruction, queryRequest.getInputText());
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
        response.setUserId(query.getUser() != null ? query.getUser().getUserId() : null);

        if (answer != null) {
            response.setAnswerId(answer.getAnswerId());
            response.setResponseText(answer.getResponseText());
            response.setSource(answer.getSource());

            // Look up audio linked to this answer
            audioRepository.findByAnswerAnswerId(answer.getAnswerId())
                    .ifPresent(audio -> {
                        response.setAudioId(audio.getAudioId());
                        response.setAudioDuration(audio.getDuration());
                    });
        }

        return response;
    }
}
