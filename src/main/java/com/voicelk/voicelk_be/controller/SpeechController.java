package com.voicelk.voicelk_be.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.voicelk.voicelk_be.dto.AudioDto;
import com.voicelk.voicelk_be.dto.TtsRequest;
import com.voicelk.voicelk_be.service.SpeechService;
import com.voicelk.voicelk_be.tts.SpeechSynthesisResult;
import com.voicelk.voicelk_be.tts.TtsBridgeClient;
import com.voicelk.voicelk_be.tts.TtsBridgeException;

/**
 * Speech endpoints of the backend.
 *
 * <p>The browser never talks to the model service directly: it asks the backend,
 * which keeps authentication, storage and the database in one place and leaves the
 * bridge reachable only from inside the network.
 */
@RestController
@RequestMapping("/api/tts")
@CrossOrigin(origins = "*")
public class SpeechController {

    @Autowired
    private SpeechService speechService;

    @Autowired
    private TtsBridgeClient ttsBridgeClient;

    /** Renders text and streams the clip straight back; nothing is stored. */
    @PostMapping(value = "/preview", produces = "audio/wav")
    public ResponseEntity<byte[]> preview(@RequestBody TtsRequest request) {
        try {
            SpeechSynthesisResult result = speechService.preview(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/wav"));
            headers.setContentLength(result.sizeInBytes());
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"preview.wav\"");
            if (result.modelVersion() != null) {
                headers.set("X-Model-Version", result.modelVersion());
            }
            if (result.durationSeconds() != null) {
                headers.set("X-Audio-Duration", String.valueOf(result.durationSeconds()));
            }

            return new ResponseEntity<>(result.audio(), headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (TtsBridgeException e) {
            throw bridgeFailure(e);
        }
    }

    /** Voices a stored answer, keeps the clip and returns its audio record. */
    @PostMapping("/answers/{answerId}")
    public ResponseEntity<AudioDto> generateForAnswer(
            @PathVariable String answerId,
            @RequestBody(required = false) TtsRequest request) {
        try {
            AudioDto audio = speechService.generateForAnswer(answerId, request);
            return ResponseEntity.ok(audio);
        } catch (TtsBridgeException e) {
            throw bridgeFailure(e);
        }
    }

    /** Liveness of the model service, for dashboards and manual checks. */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(ttsBridgeClient.health());
    }

    /** Models the bridge can serve right now. */
    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> models() {
        try {
            return ResponseEntity.ok(ttsBridgeClient.models());
        } catch (TtsBridgeException e) {
            throw bridgeFailure(e);
        }
    }

    private ResponseStatusException bridgeFailure(TtsBridgeException e) {
        HttpStatus status = e.isRetryable() ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_REQUEST;
        return new ResponseStatusException(status, e.getMessage(), e);
    }
}
