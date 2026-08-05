package com.voicelk.voicelk_be.llm;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeminiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiService.class);

    private final RestClient restClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String model;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.max-tokens:150}")
    private int maxOutputTokens;

    public GeminiService() {
        this.restClient = RestClient.builder().build();
    }

    /**
     * Sends a prompt to Gemini Flash and returns the generated text response.
     *
     * @param prompt the input text to send to Gemini
     * @return the generated response text
     */
    public String generateAnswer(String prompt) {
        String url = apiUrl + "/" + model + ":generateContent?key=" + apiKey;

        // Build request body matching Gemini API format
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "maxOutputTokens", maxOutputTokens
                )
        );

        try {
            Map response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractTextFromResponse(response);

        } catch (Exception e) {
            LOGGER.error("Error calling Gemini API: {}", e.getMessage());
            throw new RuntimeException("Failed to generate answer from Gemini: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a prompt with a system instruction for context-aware generation.
     *
     * @param systemInstruction the system-level instruction (e.g., "You are a Sinhala language tutor")
     * @param userPrompt        the user's query text
     * @return the generated response text
     */
    public String generateAnswer(String systemInstruction, String userPrompt) {
        String url = apiUrl + "/" + model + ":generateContent?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))
                ),
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", userPrompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "maxOutputTokens", maxOutputTokens
                )
        );

        try {
            Map response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractTextFromResponse(response);

        } catch (Exception e) {
            LOGGER.error("Error calling Gemini API with system instruction: {}", e.getMessage());
            throw new RuntimeException("Failed to generate answer from Gemini: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the text content from the Gemini API response.
     * Response structure: { candidates: [{ content: { parts: [{ text: "..." }] } }] }
     */
    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map response) {
        if (response == null) {
            throw new RuntimeException("Empty response from Gemini API");
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No candidates in Gemini response");
        }

        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("No parts in Gemini response");
        }

        return (String) parts.get(0).get("text");
    }
}
