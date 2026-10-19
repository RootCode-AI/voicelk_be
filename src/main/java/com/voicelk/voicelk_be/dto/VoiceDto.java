package com.voicelk.voicelk_be.dto;

/**
 * A voice as the client sees it in its voice picker.
 *
 * @param key          value to send back as the model of a speech request
 * @param name         display name
 * @param description  one line about the voice
 * @param defaultVoice whether requests that name no voice get this one
 */
public record VoiceDto(String key, String name, String description, boolean defaultVoice) {
}
