package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.DownloadLog;
import com.voicelk.voicelk_be.repository.DownloadLogRepository;
import com.voicelk.voicelk_be.service.DownloadLogService;

@Service
public class DownloadLogServiceImpl implements DownloadLogService {

    @Autowired
    private DownloadLogRepository downloadLogRepository;

    @Override
    public DownloadLog createDownloadLog(DownloadLog downloadLog) {
        return downloadLogRepository.save(downloadLog);
    }

    @Override
    public Optional<DownloadLog> getDownloadLogById(String logId) {
        return downloadLogRepository.findById(logId);
    }

    @Override
    public List<DownloadLog> getDownloadLogsByUserId(String userId) {
        return downloadLogRepository.findByUserUserId(userId);
    }

    @Override
    public List<DownloadLog> getDownloadLogsByAudioId(String audioId) {
        return downloadLogRepository.findByAudioAudioId(audioId);
    }

    @Override
    public List<DownloadLog> getAllDownloadLogs() {
        return downloadLogRepository.findAll();
    }

    @Override
    public void deleteDownloadLog(String logId) {
        if (!downloadLogRepository.existsById(logId)) {
            throw new RuntimeException("Download log not found with id: " + logId);
        }
        downloadLogRepository.deleteById(logId);
    }
}
