package com.voicelk.voicelk_be.tts;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * The voices users may choose from, as written under {@code tts.*} in the
 * application properties.
 *
 * @param defaultModel key used when a request names no voice; empty leaves the
 *                     choice to the bridge's own default
 * @param voices       every voice the backend knows about, in display order
 */
@ConfigurationProperties(prefix = "tts")
public record TtsVoiceProperties(String defaultModel, List<Voice> voices) {

    /**
     * One voice offered to users.
     *
     * @param key         model key registered in the TTS bridge
     * @param name        what the client shows
     * @param description one line about the voice, for the client
     * @param enabled     false hides the voice without deleting its entry
     */
    public record Voice(String key, String name, String description, @DefaultValue("true") boolean enabled) {
    }
}
