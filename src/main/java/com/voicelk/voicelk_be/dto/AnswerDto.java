package com.voicelk.voicelk_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {

    private String answerId;
    private String responseText;
    private String source;
    private String queryId;
}
