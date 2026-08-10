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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.entity.Audio;
import com.voicelk.voicelk_be.service.AudioService;

@RestController
@RequestMapping("/api/audios")
@CrossOrigin(origins = "*")
public class AudioController {

    @Autowired
    private AudioService audioService;

    @PostMapping
    public ResponseEntity<Audio> createAudio(@RequestBody Audio audio) {
        Audio createdAudio = audioService.createAudio(audio);
        return new ResponseEntity<>(createdAudio, HttpStatus.CREATED);
    }

    @GetMapping("/{audioId}")
    public ResponseEntity<Audio> getAudioById(@PathVariable String audioId) {
        return audioService.getAudioById(audioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/answer/{answerId}")
    public ResponseEntity<Audio> getAudioByAnswerId(@PathVariable String answerId) {
        return audioService.getAudioByAnswerId(answerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Audio>> getAllAudios() {
        return ResponseEntity.ok(audioService.getAllAudios());
    }

    @PutMapping("/{audioId}")
    public ResponseEntity<Audio> updateAudio(@PathVariable String audioId, @RequestBody Audio audio) {
        return ResponseEntity.ok(audioService.updateAudio(audioId, audio));
    }

    @DeleteMapping("/{audioId}")
    public ResponseEntity<Void> deleteAudio(@PathVariable String audioId) {
        audioService.deleteAudio(audioId);
        return ResponseEntity.noContent().build();
    }
}