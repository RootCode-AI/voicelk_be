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
public class DownloadLogDto {

    private String logId;
    private LocalDateTime date;
    private String userId;
    private String audioId;
}
