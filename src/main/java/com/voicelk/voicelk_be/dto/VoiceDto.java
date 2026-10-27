package com.voicelk.voicelk_be.dto;

import com.voicelk.voicelk_be.tts.VoiceStatus;

/**
 * A voice as the client sees it in its voice picker.
 *
 * @param key          value to send back as the model of a speech request
 * @param name         display name
 * @param description  one line about the voice
 * @param tag          optional badge shown beside the name, e.g. "New"
 * @param status       AVAILABLE, COMING_SOON or RETIRED; only an available voice can be chosen
 * @param defaultVoice whether requests that name no voice get this one
 */
public record VoiceDto(String key, String name, String description, String tag, VoiceStatus status,
        boolean defaultVoice) {
}
