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

import com.voicelk.voicelk_be.dto.DownloadLogDto;
import com.voicelk.voicelk_be.entity.DownloadLog;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.service.DownloadLogService;

@RestController
@RequestMapping("/api/download-logs")
@CrossOrigin(origins = "*")
public class DownloadLogController {

    @Autowired
    private DownloadLogService downloadLogService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<DownloadLogDto> createDownloadLog(@RequestBody DownloadLog downloadLog) {
        DownloadLog createdLog = downloadLogService.createDownloadLog(downloadLog);
        return new ResponseEntity<>(entityMapper.toDto(createdLog), HttpStatus.CREATED);
    }

    @GetMapping("/{logId}")
    public ResponseEntity<DownloadLogDto> getDownloadLogById(@PathVariable String logId) {
        return downloadLogService.getDownloadLogById(logId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DownloadLogDto>> getDownloadLogsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(entityMapper.toDownloadLogDtoList(downloadLogService.getDownloadLogsByUserId(userId)));
    }

    @GetMapping("/audio/{audioId}")
    public ResponseEntity<List<DownloadLogDto>> getDownloadLogsByAudioId(@PathVariable String audioId) {
        return ResponseEntity
                .ok(entityMapper.toDownloadLogDtoList(downloadLogService.getDownloadLogsByAudioId(audioId)));
    }

    @GetMapping
    public ResponseEntity<List<DownloadLogDto>> getAllDownloadLogs() {
        return ResponseEntity.ok(entityMapper.toDownloadLogDtoList(downloadLogService.getAllDownloadLogs()));
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteDownloadLog(@PathVariable String logId) {
        downloadLogService.deleteDownloadLog(logId);
        return ResponseEntity.noContent().build();
    }
}
