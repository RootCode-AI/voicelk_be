package com.voicelk.voicelk_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.Audio;

@Repository
public interface AudioRepository extends JpaRepository<Audio, String> {

    Optional<Audio> findByAnswerAnswerId(String answerId);
}
