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
public class UserFeedbackDto {

    private String feedbackId;
    private int rating;
    private String comment;
    private LocalDateTime timestamp;
    private String registeredUserId;
    private String audioId;
}
