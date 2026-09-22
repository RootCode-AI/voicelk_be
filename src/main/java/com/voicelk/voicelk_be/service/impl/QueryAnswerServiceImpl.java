package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.voicelk.voicelk_be.llm.PlainTextFormatter;
import com.voicelk.voicelk_be.repository.AnswerRepository;
import com.voicelk.voicelk_be.repository.AudioRepository;
import com.voicelk.voicelk_be.repository.GuestUserRepository;
import com.voicelk.voicelk_be.repository.QueryRepository;
import com.voicelk.voicelk_be.repository.UserRepository;
import com.voicelk.voicelk_be.service.QueryAnswerService;
import com.voicelk.voicelk_be.service.SpeechService;

@Service
public class QueryAnswerServiceImpl implements QueryAnswerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryAnswerServiceImpl.class);

    /** The clip exists in storage and its record is saved. */
    private static final String AUDIO_GENERATED = "GENERATED";
    /** Generation was attempted for this request and failed; see audioError. */
    private static final String AUDIO_FAILED = "FAILED";
    /** Automatic generation is switched off (tts.auto-generate=false). */
    private static final String AUDIO_DISABLED = "DISABLED";
    /** The question came from a guest, who receives the text answer only. */
    private static final String AUDIO_GUEST = "GUEST_TEXT_ONLY";
    /** No clip is stored for this answer. */
    private static final String AUDIO_MISSING = "NOT_GENERATED";

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

    @Autowired
    private SpeechService speechService;

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
                + "- The explanation must be simple and easy to understand for O/L and A/L students.\n"
                + "- The answer is read aloud by a text-to-speech system, so reply in plain text only: "
                + "no Markdown, no bold or italics, no asterisks, no headings, no bullet or numbered lists, "
                + "no tables, no emojis and no code. Write full sentences in one or two short paragraphs.\n";

        String generatedText;
        if (queryRequest.getSyllabusTopic() != null && !queryRequest.getSyllabusTopic().isEmpty()) {
            systemInstruction += "Answer the question related to the topic: " + queryRequest.getSyllabusTopic() + ".";
            generatedText = geminiService.generateAnswer(systemInstruction, queryRequest.getInputText());
        } else {
            generatedText = geminiService.generateAnswer(systemInstruction, queryRequest.getInputText());
        }

        // The prompt asks for plain text, but the model does not always comply; the
        // stored answer is what gets voiced, so any leftover markup is removed here.
        generatedText = PlainTextFormatter.toPlainText(generatedText);

        Answer answer = new Answer();
        answer.setResponseText(generatedText);
        answer.setSource("Gemini Flash");
        answer.setQuery(query);
        answer = answerRepository.save(answer);

        // Voicing the answer never blocks the text reply: when the model service or
        // storage fails the user still gets the answer, the reason travels back in
        // audioError, and the clip can be requested again through the speech endpoints.
        // Guests get the text answer only; voicing is a registered-user feature.
        boolean guest = user instanceof GuestUser;
        String audioError = null;
        if (speechService.isAutoGenerationEnabled() && !guest) {
            try {
                speechService.generateForAnswer(answer.getAnswerId(), null);
            } catch (Exception e) {
                audioError = e.getMessage();
                LOGGER.warn("Audio was not generated for answer {}: {}", answer.getAnswerId(), audioError, e);
            }
        }

        QueryResponse response = mapToResponse(query, answer);
        if (guest) {
            response.setAudioStatus(AUDIO_GUEST);
        } else if (audioError != null) {
            response.setAudioStatus(AUDIO_FAILED);
            response.setAudioError(audioError);
        } else if (!speechService.isAutoGenerationEnabled() && response.getAudioId() == null) {
            response.setAudioStatus(AUDIO_DISABLED);
        }
        return response;
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
            response.setAudioStatus(AUDIO_MISSING);
            audioRepository.findByAnswerAnswerId(answer.getAnswerId())
                    .ifPresent(audio -> {
                        response.setAudioStatus(AUDIO_GENERATED);
                        response.setAudioId(audio.getAudioId());
                        response.setAudioUrl(audio.getFilePath());
                        response.setAudioDuration(audio.getDuration());
                        response.setAudioModelVersion(audio.getModelVersion());
                    });
        }

        return response;
    }
}
