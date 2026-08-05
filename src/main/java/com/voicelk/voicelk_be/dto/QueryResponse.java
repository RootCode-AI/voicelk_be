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
}
