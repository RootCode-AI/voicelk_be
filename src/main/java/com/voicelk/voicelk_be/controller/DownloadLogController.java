package com.voicelk.voicelk_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.entity.DownloadLog;
import com.voicelk.voicelk_be.service.DownloadLogService;

@RestController
@RequestMapping("/api/download-logs")
@CrossOrigin(origins = "*")
public class DownloadLogController {

    @Autowired
    private DownloadLogService downloadLogService;

    @PostMapping
    public ResponseEntity<DownloadLog> createDownloadLog(@RequestBody DownloadLog downloadLog) {
        DownloadLog createdLog = downloadLogService.createDownloadLog(downloadLog);
        return new ResponseEntity<>(createdLog, HttpStatus.CREATED);
    }

    @GetMapping("/{logId}")
    public ResponseEntity<DownloadLog> getDownloadLogById(@PathVariable String logId) {
        return downloadLogService.getDownloadLogById(logId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DownloadLog>> getDownloadLogsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(downloadLogService.getDownloadLogsByUserId(userId));
    }

    @GetMapping("/audio/{audioId}")
    public ResponseEntity<List<DownloadLog>> getDownloadLogsByAudioId(@PathVariable String audioId) {
        return ResponseEntity.ok(downloadLogService.getDownloadLogsByAudioId(audioId));
    }

    @GetMapping
    public ResponseEntity<List<DownloadLog>> getAllDownloadLogs() {
        return ResponseEntity.ok(downloadLogService.getAllDownloadLogs());
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteDownloadLog(@PathVariable String logId) {
        downloadLogService.deleteDownloadLog(logId);
        return ResponseEntity.noContent().build();
    }
}
