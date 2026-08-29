package com.voicelk.voicelk_be.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AudioDto {

    private String audioId;
    private String filePath;
    private String format;
    private Double duration;
    private String modelVersion;
    private Double processingTime;
    private String answerId;
    private List<String> downloadLogIds;
    private String userFeedbackId;
}
