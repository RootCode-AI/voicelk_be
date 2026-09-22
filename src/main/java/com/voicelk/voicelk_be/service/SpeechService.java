package com.voicelk.voicelk_be.service;

import com.voicelk.voicelk_be.dto.AudioDto;
import com.voicelk.voicelk_be.dto.TtsRequest;
import com.voicelk.voicelk_be.tts.SpeechSynthesisResult;

/**
 * Speech generation as the rest of the backend sees it: text in, a stored audio
 * record out. The transport to the model service lives behind this interface.
 */
public interface SpeechService {

    /** Renders text and returns the clip without storing anything. */
    SpeechSynthesisResult preview(TtsRequest request);

    /** Renders the stored answer, uploads the clip and saves its audio record. */
    AudioDto generateForAnswer(String answerId, TtsRequest request);

    /** Whether audio should be produced automatically for every new answer. */
    boolean isAutoGenerationEnabled();
}
