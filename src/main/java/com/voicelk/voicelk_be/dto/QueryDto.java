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
public class QueryDto {

    private String queryId;
    private String inputText;
    private LocalDateTime timestamp;
    private String syllabusTopic;
    private String userId;
    private String answerId;
}
