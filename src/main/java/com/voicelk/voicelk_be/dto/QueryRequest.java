package com.voicelk.voicelk_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {

    private String inputText;
    private String syllabusTopic;
    private String userId;
    private String sessionId;
}
