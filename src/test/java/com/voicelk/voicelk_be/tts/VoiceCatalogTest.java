package com.voicelk.voicelk_be.tts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class VoiceCatalogTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(VoiceCatalog.class);

    private static final String[] VOICES = {
            "tts.default-model=speaker_03",
            "tts.voices[0].key=speaker_03",
            "tts.voices[0].name=Speaker 03",
            "tts.voices[1].key=speaker_01",
            "tts.voices[1].name=Speaker 01",
            "tts.voices[2].key=speaker_02",
            "tts.voices[2].enabled=false",
    };

    @Test
    void bindsVoicesFromPropertiesAndHidesDisabledOnes() {
        runner.withPropertyValues(VOICES).run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            assertEquals(List.of("speaker_03", "speaker_01"),
                    catalog.voices().stream().map(TtsVoiceProperties.Voice::key).toList());
            assertEquals("Speaker 01", catalog.voices().get(1).name());
            assertEquals("speaker_03", catalog.defaultKey());
        });
    }

    @Test
    void resolvesKnownVoicesAndFallsBackToTheDefault() {
        runner.withPropertyValues(VOICES).run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            assertEquals("speaker_01", catalog.resolve("speaker_01"));
            assertEquals("speaker_03", catalog.resolve(null));
            assertEquals("speaker_03", catalog.resolve(" "));
        });
    }

    @Test
    void refusesUnknownAndDisabledVoices() {
        runner.withPropertyValues(VOICES).run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            assertThrows(IllegalArgumentException.class, () -> catalog.resolve("speaker_02"));
            assertThrows(IllegalArgumentException.class, () -> catalog.resolve("missing"));
        });
    }

    @Test
    void refusesADefaultThatIsNotOnOffer() {
        runner.withPropertyValues("tts.default-model=speaker_02", "tts.voices[0].key=speaker_03")
                .run(context -> assertTrue(context.getStartupFailure() != null));
    }

    @Test
    void passesAnyKeyThroughWhenNoVoicesAreConfigured() {
        runner.run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            assertEquals("custom", catalog.resolve("custom"));
            assertNull(catalog.resolve(null));
        });
    }
}
