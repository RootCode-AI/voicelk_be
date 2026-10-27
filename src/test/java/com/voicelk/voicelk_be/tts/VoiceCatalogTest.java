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
            "tts.voices[1].key=speaker_02_v2",
            "tts.voices[1].name=Speaker 02 V2.0",
            "tts.voices[1].tag=New",
            "tts.voices[2].key=speaker_02_v2_1",
            "tts.voices[2].name=Speaker 02 V2.1",
            "tts.voices[2].status=COMING_SOON",
            "tts.voices[3].key=speaker_01",
            "tts.voices[3].name=Speaker 01",
            "tts.voices[3].status=RETIRED",
            "tts.voices[4].key=hidden",
            "tts.voices[4].enabled=false",
    };

    @Test
    void bindsVoicesFromPropertiesAndHidesDisabledOnes() {
        runner.withPropertyValues(VOICES).run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            assertEquals(List.of("speaker_03", "speaker_02_v2", "speaker_02_v2_1", "speaker_01"),
                    catalog.voices().stream().map(TtsVoiceProperties.Voice::key).toList());
            assertEquals("Speaker 02 V2.0", catalog.voices().get(1).name());
            assertEquals("New", catalog.voices().get(1).tag());
            assertEquals("speaker_03", catalog.defaultKey());
        });
    }

    @Test
    void bindsStatusesWithAvailableAsTheDefault() {
        runner.withPropertyValues(VOICES).run(context -> {
            List<TtsVoiceProperties.Voice> voices = context.getBean(VoiceCatalog.class).voices();
            assertEquals(List.of(VoiceStatus.AVAILABLE, VoiceStatus.AVAILABLE, VoiceStatus.COMING_SOON, VoiceStatus.RETIRED),
                    voices.stream().map(TtsVoiceProperties.Voice::status).toList());
        });
    }

    @Test
    void resolvesKnownVoicesAndFallsBackToTheDefault() {
        runner.withPropertyValues(VOICES).run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            assertEquals("speaker_02_v2", catalog.resolve("speaker_02_v2"));
            assertEquals("speaker_03", catalog.resolve(null));
            assertEquals("speaker_03", catalog.resolve(" "));
        });
    }

    @Test
    void refusesUnknownAndDisabledVoices() {
        runner.withPropertyValues(VOICES).run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            assertThrows(IllegalArgumentException.class, () -> catalog.resolve("hidden"));
            assertThrows(IllegalArgumentException.class, () -> catalog.resolve("missing"));
        });
    }

    @Test
    void refusesRetiredAndComingSoonVoicesWithAReason() {
        runner.withPropertyValues(VOICES).run(context -> {
            VoiceCatalog catalog = context.getBean(VoiceCatalog.class);
            IllegalArgumentException retired = assertThrows(IllegalArgumentException.class,
                    () -> catalog.resolve("speaker_01"));
            assertTrue(retired.getMessage().startsWith("Speaker 01 is no longer supported."));
            IllegalArgumentException upcoming = assertThrows(IllegalArgumentException.class,
                    () -> catalog.resolve("speaker_02_v2_1"));
            assertTrue(upcoming.getMessage().startsWith("Speaker 02 V2.1 is coming soon"));
            assertTrue(upcoming.getMessage().endsWith("Available voices: speaker_03, speaker_02_v2."));
        });
    }

    @Test
    void refusesADefaultThatIsNotOnOffer() {
        runner.withPropertyValues("tts.default-model=speaker_02", "tts.voices[0].key=speaker_03")
                .run(context -> assertTrue(context.getStartupFailure() != null));
    }

    @Test
    void refusesARetiredDefault() {
        runner.withPropertyValues("tts.default-model=speaker_01",
                        "tts.voices[0].key=speaker_03",
                        "tts.voices[1].key=speaker_01",
                        "tts.voices[1].status=RETIRED")
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
