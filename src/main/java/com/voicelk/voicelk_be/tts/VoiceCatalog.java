package com.voicelk.voicelk_be.tts;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.voicelk.voicelk_be.tts.TtsVoiceProperties.Voice;

/**
 * The voices a client may ask for, and the gate every speech request passes through.
 *
 * <p>A request names a voice by its key; that key is checked here before it reaches
 * the bridge, so a client can only select a model that is listed, enabled and
 * available in the configuration. Voices that are coming soon or retired are still
 * listed, so the client can announce them, but no speech is made with them. With no
 * voices configured the check is skipped and the key goes to the bridge as given.
 */
@Component
@EnableConfigurationProperties(TtsVoiceProperties.class)
public class VoiceCatalog {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoiceCatalog.class);

    private final List<Voice> voices;
    private final String defaultKey;

    public VoiceCatalog(TtsVoiceProperties properties) {
        List<Voice> configured = properties.voices() == null ? List.of() : properties.voices();

        Set<String> seen = new HashSet<>();
        for (Voice voice : configured) {
            if (voice.key() == null || voice.key().isBlank()) {
                throw new IllegalStateException("Every entry under tts.voices needs a key.");
            }
            if (!seen.add(voice.key())) {
                throw new IllegalStateException("The voice key '" + voice.key() + "' is listed twice under tts.voices.");
            }
        }

        this.voices = configured.stream().filter(Voice::enabled).toList();

        String requestedDefault = properties.defaultModel();
        if (requestedDefault == null || requestedDefault.isBlank()) {
            this.defaultKey = null;
        } else if (voices.isEmpty() || find(requestedDefault).filter(Voice::selectable).isPresent()) {
            this.defaultKey = requestedDefault;
        } else {
            // A default that cannot be selected would fail every request that names no
            // voice, so it is refused at startup rather than on the first question.
            throw new IllegalStateException("tts.default-model '" + requestedDefault
                    + "' is not an enabled, available voice under tts.voices.");
        }

        LOGGER.info("Voices on offer: {} (default {})",
                voices.isEmpty() ? "any the bridge serves" : keys(),
                defaultKey == null ? "chosen by the bridge" : defaultKey);
    }

    /** Enabled voices in display order, including those coming soon or retired. */
    public List<Voice> voices() {
        return voices;
    }

    /** The key used when a request names no voice, or null to let the bridge decide. */
    public String defaultKey() {
        return defaultKey;
    }

    /**
     * The model key to send to the bridge for a requested voice.
     *
     * @throws IllegalArgumentException when the voice is not on offer
     */
    public String resolve(String requested) {
        if (requested == null || requested.isBlank()) {
            return defaultKey;
        }
        if (voices.isEmpty()) {
            return requested;
        }
        Voice voice = find(requested).orElseThrow(() -> new IllegalArgumentException(
                "Unknown voice '" + requested + "'. Available voices: " + keys() + "."));
        return switch (voice.status()) {
            case AVAILABLE -> voice.key();
            case COMING_SOON -> throw new IllegalArgumentException(
                    displayName(voice) + " is coming soon and cannot be used yet. Available voices: " + keys() + ".");
            case RETIRED -> throw new IllegalArgumentException(
                    displayName(voice) + " is no longer supported. Available voices: " + keys() + ".");
        };
    }

    private Optional<Voice> find(String key) {
        return voices.stream().filter(voice -> voice.key().equals(key)).findFirst();
    }

    /** Keys that can actually be used, for log lines and error messages. */
    private String keys() {
        return voices.stream().filter(Voice::selectable).map(Voice::key).collect(Collectors.joining(", "));
    }

    private static String displayName(Voice voice) {
        return voice.name() == null || voice.name().isBlank() ? "The voice '" + voice.key() + "'" : voice.name();
    }
}
