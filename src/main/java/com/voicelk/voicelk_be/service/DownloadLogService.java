package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.DownloadLog;

@Service
public interface DownloadLogService {

    DownloadLog createDownloadLog(DownloadLog downloadLog);

    Optional<DownloadLog> getDownloadLogById(String logId);

    List<DownloadLog> getDownloadLogsByUserId(String userId);

    List<DownloadLog> getDownloadLogsByAudioId(String audioId);

    List<DownloadLog> getAllDownloadLogs();

    void deleteDownloadLog(String logId);
}
