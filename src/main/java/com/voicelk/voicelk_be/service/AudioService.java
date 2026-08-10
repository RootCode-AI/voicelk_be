package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.Audio;

@Service
public interface AudioService {

    Audio createAudio(Audio audio);

    Optional<Audio> getAudioById(String audioId);

    Optional<Audio> getAudioByAnswerId(String answerId);

    List<Audio> getAllAudios();

    Audio updateAudio(String audioId, Audio audio);

    void deleteAudio(String audioId);
}
