package com.voicelk.voicelk_be.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.dto.AudioDto;
import com.voicelk.voicelk_be.dto.TtsRequest;
import com.voicelk.voicelk_be.entity.Answer;
import com.voicelk.voicelk_be.entity.Audio;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.repository.AnswerRepository;
import com.voicelk.voicelk_be.repository.AudioRepository;
import com.voicelk.voicelk_be.service.SpeechService;
import com.voicelk.voicelk_be.service.SupabaseStorageService;
import com.voicelk.voicelk_be.tts.SpeechSynthesisResult;
import com.voicelk.voicelk_be.tts.TtsBridgeClient;

/**
 * Orchestrates one round of speech generation.
 *
 * <p>The division of labour across the two services is deliberate: the bridge only
 * turns text into bytes, while storage and the database stay here, so the model
 * service never needs storage credentials and can be moved to a GPU machine on its
 * own.
 */
@Service
public class SpeechServiceImpl implements SpeechService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeechServiceImpl.class);

    private static final String AUDIO_FORMAT = "wav";

    @Autowired
    private TtsBridgeClient ttsBridgeClient;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AudioRepository audioRepository;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Autowired
    private EntityMapper entityMapper;

    @Value("${tts.storage.bucket:audios}")
    private String bucketName;

    @Value("${tts.storage.path-prefix:tts}")
    private String pathPrefix;

    @Value("${tts.auto-generate:true}")
    private boolean autoGenerate;

    @Value("${tts.default-model:}")
    private String defaultModel;

    @Value("${tts.default-speaker-id:0}")
    private Integer defaultSpeakerId;

    @Override
    public boolean isAutoGenerationEnabled() {
        return autoGenerate;
    }

    @Override
    public SpeechSynthesisResult preview(TtsRequest request) {
        String text = request == null ? null : request.getText();
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text is required for a speech preview.");
        }
        return synthesize(text, request);
    }

    @Override
    public AudioDto generateForAnswer(String answerId, TtsRequest request) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found with id: " + answerId));

        TtsRequest options = request == null ? new TtsRequest() : request;

        Optional<Audio> existing = audioRepository.findByAnswerAnswerId(answerId);
        if (existing.isPresent() && !options.isRegenerate()) {
            LOGGER.debug("Answer {} already has audio; returning the stored clip.", answerId);
            return entityMapper.toDto(existing.get());
        }

        // An explicit text overrides the stored answer, which is useful for fixing a
        // pronunciation without changing what the user was told.
        String text = options.getText() != null && !options.getText().isBlank()
                ? options.getText()
                : answer.getResponseText();

        SpeechSynthesisResult result = synthesize(text, options);
        String fileUrl = upload(result, answerId);

        Audio audio = existing.orElseGet(Audio::new);
        audio.setFilePath(fileUrl);
        audio.setFormat(AUDIO_FORMAT);
        audio.setDuration(result.durationSeconds());
        audio.setModelVersion(result.modelVersion());
        audio.setProcessingTime(result.processingTime());
        audio.setAnswer(answer);

        Audio saved = audioRepository.save(audio);
        LOGGER.info("Stored {} s of audio for answer {} ({} bytes).",
                result.durationSeconds(), answerId, result.sizeInBytes());

        return entityMapper.toDto(saved);
    }

    // -----------------------------------------------------------------------
    // helpers
    // -----------------------------------------------------------------------

    private SpeechSynthesisResult synthesize(String text, TtsRequest options) {
        String model = options != null && options.getModel() != null && !options.getModel().isBlank()
                ? options.getModel()
                : (defaultModel == null || defaultModel.isBlank() ? null : defaultModel);

        Integer speakerId = options != null && options.getSpeakerId() != null
                ? options.getSpeakerId()
                : defaultSpeakerId;

        Double speed = options == null ? null : options.getSpeed();

        return ttsBridgeClient.synthesize(text, model, speakerId, speed);
    }

    private String upload(SpeechSynthesisResult result, String answerId) {
        try {
            return supabaseStorageService.uploadBytes(
                    result.audio(),
                    answerId + "." + AUDIO_FORMAT,
                    result.contentType(),
                    bucketName,
                    pathPrefix);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store the generated audio: " + e.getMessage(), e);
        }
    }
}
