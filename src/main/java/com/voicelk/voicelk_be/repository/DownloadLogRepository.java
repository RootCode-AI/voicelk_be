package com.voicelk.voicelk_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.DownloadLog;

@Repository
public interface DownloadLogRepository extends JpaRepository<DownloadLog, String> {

    List<DownloadLog> findByUserUserId(String userId);

    List<DownloadLog> findByAudioAudioId(String audioId);
}
