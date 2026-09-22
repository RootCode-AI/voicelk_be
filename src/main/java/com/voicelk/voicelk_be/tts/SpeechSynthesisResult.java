package com.voicelk.voicelk_be.tts;

/**
 * One rendered clip as it comes back from the TTS bridge.
 *
 * @param audio           the raw WAV bytes
 * @param contentType     media type reported by the bridge
 * @param modelKey        registry key of the model that produced the clip
 * @param modelVersion    full identity of the weights, stored with the audio record
 * @param durationSeconds length of the generated speech
 * @param processingTime  how long the bridge took to render it
 * @param sampleRate      sample rate of the waveform
 * @param normalizedText  text after normalisation, useful for debugging odd output
 * @param ipaSequence     phoneme string that was fed to the acoustic model
 */
public record SpeechSynthesisResult(
        byte[] audio,
        String contentType,
        String modelKey,
        String modelVersion,
        Double durationSeconds,
        Double processingTime,
        Integer sampleRate,
        String normalizedText,
        String ipaSequence) {

    public int sizeInBytes() {
        return audio == null ? 0 : audio.length;
    }
}
