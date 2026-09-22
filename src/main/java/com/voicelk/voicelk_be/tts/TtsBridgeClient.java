package com.voicelk.voicelk_be.tts;

import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * HTTP client for the VoiceLK TTS bridge — the Python microservice that owns the
 * acoustic model.
 *
 * <p>This class is the only place in the backend that knows the bridge's wire format.
 * Synthesis is slow compared to an ordinary API call (the first request also pays for
 * loading a checkpoint), so the read timeout is generous and configured separately from
 * the connect timeout.
 */
@Component
public class TtsBridgeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TtsBridgeClient.class);

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String SYNTHESIZE_PATH = "/api/v1/synthesize";
    private static final String PHONEMES_PATH = "/api/v1/phonemes";
    private static final String MODELS_PATH = "/api/v1/models";
    private static final String HEALTH_PATH = "/health";

    private final RestClient restClient;
    private final String apiKey;
    private final String baseUrl;

    public TtsBridgeClient(
            @Value("${tts.bridge.url:http://127.0.0.1:8000}") String baseUrl,
            @Value("${tts.bridge.api-key:}") String apiKey,
            @Value("${tts.bridge.connect-timeout-ms:5000}") long connectTimeoutMs,
            @Value("${tts.bridge.read-timeout-ms:180000}") long readTimeoutMs) {

        this.baseUrl = baseUrl;
        this.apiKey = apiKey;

        // HTTP/1.1 only: by default the JDK client offers an h2c upgrade on plain http,
        // and uvicorn then discards the request body, so every call fails with 422.
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        LOGGER.info("TTS bridge client configured for {} (read timeout {} ms)", baseUrl, readTimeoutMs);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Renders text to speech and returns the clip together with the metadata the
     * bridge reports in response headers.
     */
    public SpeechSynthesisResult synthesize(String text, String model, Integer speakerId, Double speed) {
        if (text == null || text.isBlank()) {
            throw new TtsBridgeException("Cannot synthesize empty text.", false);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("text", text);
        if (model != null && !model.isBlank()) {
            payload.put("model", model);
        }
        payload.put("speaker_id", speakerId == null ? 0 : speakerId);
        payload.put("speed", speed == null ? 1.0d : speed);

        try {
            ResponseEntity<byte[]> response = restClient.post()
                    .uri(SYNTHESIZE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.parseMediaType("audio/wav"), MediaType.APPLICATION_JSON)
                    .headers(this::applyApiKey)
                    .body(payload)
                    .retrieve()
                    .toEntity(byte[].class);

            byte[] audio = response.getBody();
            if (audio == null || audio.length == 0) {
                throw new TtsBridgeException("The TTS bridge returned an empty audio body.", true);
            }

            HttpHeaders headers = response.getHeaders();
            MediaType contentType = headers.getContentType();

            return new SpeechSynthesisResult(
                    audio,
                    contentType == null ? "audio/wav" : contentType.toString(),
                    headers.getFirst("X-Model-Key"),
                    headers.getFirst("X-Model-Version"),
                    parseDouble(headers.getFirst("X-Audio-Duration")),
                    parseDouble(headers.getFirst("X-Processing-Time")),
                    parseInteger(headers.getFirst("X-Sample-Rate")),
                    decode(headers.getFirst("X-Normalized-Text")),
                    decode(headers.getFirst("X-Ipa-Sequence")));

        } catch (RestClientResponseException e) {
            throw translate(e);
        } catch (ResourceAccessException e) {
            throw unreachable(e);
        }
    }

    /** Normalisation and grapheme-to-phoneme only — no audio is generated. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> phonemes(String text) {
        try {
            return restClient.post()
                    .uri(PHONEMES_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(this::applyApiKey)
                    .body(Map.of("text", text))
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientResponseException e) {
            throw translate(e);
        } catch (ResourceAccessException e) {
            throw unreachable(e);
        }
    }

    /** Models the bridge is willing to serve, and which of them are already warm. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> models() {
        try {
            return restClient.get()
                    .uri(MODELS_PATH)
                    .headers(this::applyApiKey)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientResponseException e) {
            throw translate(e);
        } catch (ResourceAccessException e) {
            throw unreachable(e);
        }
    }

    /** Liveness of the bridge; never throws for an unreachable service. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> health() {
        try {
            Map<String, Object> body = restClient.get()
                    .uri(HEALTH_PATH)
                    .retrieve()
                    .body(Map.class);
            return body == null ? Map.of("status", "unknown", "bridgeUrl", baseUrl) : body;
        } catch (Exception e) {
            LOGGER.warn("TTS bridge health check failed: {}", e.getMessage());
            return Map.of(
                    "status", "unreachable",
                    "bridgeUrl", baseUrl,
                    "detail", String.valueOf(e.getMessage()));
        }
    }

    // -----------------------------------------------------------------------
    // helpers
    // -----------------------------------------------------------------------

    private void applyApiKey(HttpHeaders headers) {
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set(API_KEY_HEADER, apiKey);
        }
    }

    private TtsBridgeException translate(RestClientResponseException e) {
        String body = e.getResponseBodyAsString();
        int status = e.getStatusCode().value();
        LOGGER.error("TTS bridge rejected the request ({}): {}", status, body);

        // 5xx means the bridge is there but unhappy — worth retrying later;
        // 4xx means this exact request is wrong and will stay wrong.
        boolean retryable = e.getStatusCode().is5xxServerError();
        return new TtsBridgeException(
                "TTS bridge responded with status " + status + ": " + body, retryable, e);
    }

    private TtsBridgeException unreachable(ResourceAccessException e) {
        LOGGER.error("TTS bridge at {} is unreachable: {}", baseUrl, e.getMessage());
        return new TtsBridgeException(
                "The TTS bridge at " + baseUrl + " is unreachable. Is the Python service running?",
                true, e);
    }

    private static Double parseDouble(String value) {
        try {
            return value == null ? null : Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseInteger(String value) {
        try {
            return value == null ? null : Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Sinhala and IPA travel percent-encoded because headers are ASCII-only. */
    private static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return value;
        }
    }
}
