package com.voicelk.voicelk_be.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {

    private String queryId;
    private String inputText;
    private String syllabusTopic;
    private LocalDateTime timestamp;
    private String userId;
    private String answerId;
    private String responseText;
    private String source;
    private String audioId;
    private String audioUrl;
    /** GENERATED, FAILED, DISABLED, GUEST_TEXT_ONLY, GUEST_LIMIT_REACHED or NOT_GENERATED. */
    private String audioStatus;
    /** Why generation failed; only set when audioStatus is FAILED. */
    private String audioError;
    private String audioModelVersion;
    private Double audioDuration;
    /** How many free clips a guest may still generate; null for registered users. */
    private Integer guestAudioRemaining;
    /** The free-clip allowance for guests; null for registered users. */
    private Integer guestAudioLimit;
}
