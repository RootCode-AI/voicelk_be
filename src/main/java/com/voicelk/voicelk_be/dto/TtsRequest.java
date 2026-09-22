package com.voicelk.voicelk_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * What a client may ask of the speech endpoints.
 *
 * <p>Every field except the text is optional: leaving them out gives the bridge's
 * configured default model, the first speaker and the model's natural pace. When
 * an answer is being voiced, the text is taken from the stored answer instead.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TtsRequest {

    private String text;

    /** Registry key of the model to use, e.g. the corpus a run was trained on. */
    private String model;

    /** Speaker index for multi-speaker checkpoints. */
    private Integer speakerId;

    /** Speaking-rate multiplier; 1.0 is the pace the model was trained at. */
    private Double speed;

    /** Render again even when this answer already has audio stored. */
    private boolean regenerate;
}
