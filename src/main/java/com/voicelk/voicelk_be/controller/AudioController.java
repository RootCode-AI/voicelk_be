package com.voicelk.voicelk_be.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.dto.AudioDto;
import com.voicelk.voicelk_be.entity.Audio;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.service.AudioService;
import com.voicelk.voicelk_be.service.SupabaseStorageService;

@RestController
@RequestMapping("/api/audios")
@CrossOrigin(origins = "*")
public class AudioController {

    @Autowired
    private AudioService audioService;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<AudioDto> createAudio(@RequestBody Audio audio) {
        Audio createdAudio = audioService.createAudio(audio);
        return new ResponseEntity<>(entityMapper.toDto(createdAudio), HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioDto> uploadAudio(
            @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @org.springframework.web.bind.annotation.RequestParam("answerId") String answerId,
            @org.springframework.web.bind.annotation.RequestParam(value = "format", required = false) String format,
            @org.springframework.web.bind.annotation.RequestParam(value = "duration", required = false) Double duration,
            @org.springframework.web.bind.annotation.RequestParam(value = "modelVersion", required = false) String modelVersion,
            @org.springframework.web.bind.annotation.RequestParam(value = "processingTime", required = false) Double processingTime) {

        try {
            // 1. Upload to Supabase Storage
            String fileUrl = supabaseStorageService.uploadFile(file, "audios", "uploads");

            // 2. Save Audio metadata in database
            Audio audio = new Audio();
            audio.setFilePath(fileUrl);
            audio.setFormat(format != null ? format : "wav");
            audio.setDuration(duration);
            audio.setModelVersion(modelVersion);
            audio.setProcessingTime(processingTime);

            com.voicelk.voicelk_be.entity.Answer answer = new com.voicelk.voicelk_be.entity.Answer();
            answer.setAnswerId(answerId);
            audio.setAnswer(answer);

            Audio createdAudio = audioService.createAudio(audio);
            return new ResponseEntity<>(entityMapper.toDto(createdAudio), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    @GetMapping("/{audioId}")
    public ResponseEntity<AudioDto> getAudioById(@PathVariable String audioId) {
        return audioService.getAudioById(audioId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/answer/{answerId}")
    public ResponseEntity<AudioDto> getAudioByAnswerId(@PathVariable String answerId) {
        return audioService.getAudioByAnswerId(answerId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AudioDto>> getAllAudios() {
        return ResponseEntity.ok(entityMapper.toAudioDtoList(audioService.getAllAudios()));
    }

    @PutMapping("/{audioId}")
    public ResponseEntity<AudioDto> updateAudio(@PathVariable String audioId, @RequestBody Audio audio) {
        return ResponseEntity.ok(entityMapper.toDto(audioService.updateAudio(audioId, audio)));
    }

    @DeleteMapping("/{audioId}")
    public ResponseEntity<Void> deleteAudio(@PathVariable String audioId) {
        audioService.deleteAudio(audioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Stream audio file for browser playback.
     * Returns the audio binary with the appropriate content type.
     */
    @GetMapping("/{audioId}/stream")
    public ResponseEntity<?> streamAudio(@PathVariable String audioId) {
        Audio audio = audioService.getAudioById(audioId)
                .orElse(null);

        if (audio == null || audio.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        String path = audio.getFilePath();

        // If the file path is a URL (e.g., Supabase storage URL), redirect to it
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(java.net.URI.create(path))
                    .build();
        }

        Path filePath = Paths.get(path);
        File file = filePath.toFile();

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        // Detect content type from file extension
        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            contentType = null;
        }
        if (contentType == null) {
            // Fallback based on format field or default to mpeg
            String format = audio.getFormat();
            if ("wav".equalsIgnoreCase(format)) {
                contentType = "audio/wav";
            } else if ("ogg".equalsIgnoreCase(format)) {
                contentType = "audio/ogg";
            } else {
                contentType = "audio/mpeg";
            }
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(file.length())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}