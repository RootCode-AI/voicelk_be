package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.Audio;
import com.voicelk.voicelk_be.repository.AudioRepository;
import com.voicelk.voicelk_be.service.AudioService;

@Service
public class AudioServiceImpl implements AudioService {

    @Autowired
    private AudioRepository audioRepository;

    @Override
    public Audio createAudio(Audio audio) {
        return audioRepository.save(audio);
    }

    @Override
    public Optional<Audio> getAudioById(String audioId) {
        return audioRepository.findById(audioId);
    }

    @Override
    public Optional<Audio> getAudioByAnswerId(String answerId) {
        return audioRepository.findByAnswerAnswerId(answerId);
    }

    @Override
    public List<Audio> getAllAudios() {
        return audioRepository.findAll();
    }

    @Override
    public Audio updateAudio(String audioId, Audio audio) {
        Audio existingAudio = audioRepository.findById(audioId)
                .orElseThrow(() -> new RuntimeException("Audio not found with id: " + audioId));

        existingAudio.setFilePath(audio.getFilePath());
        existingAudio.setFormat(audio.getFormat());
        existingAudio.setDuration(audio.getDuration());
        existingAudio.setModelVersion(audio.getModelVersion());
        existingAudio.setProcessingTime(audio.getProcessingTime());

        return audioRepository.save(existingAudio);
    }

    @Override
    public void deleteAudio(String audioId) {
        if (!audioRepository.existsById(audioId)) {
            throw new RuntimeException("Audio not found with id: " + audioId);
        }
        audioRepository.deleteById(audioId);
    }
}
