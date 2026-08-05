package com.voicelk.voicelk_be.entity;

import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"answer", "downloadLogs", "userFeedback"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audios")
public class Audio {

    @Id
    @Column(name = "audio_id", nullable = false, updatable = false, unique = true)
    private String audioId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "format")
    private String format;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "processing_time")
    private Double processingTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false, unique = true)
    private Answer answer;

    @OneToMany(mappedBy = "audio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DownloadLog> downloadLogs;

    @OneToOne(mappedBy = "audio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserFeedback userFeedback;

    @PrePersist
    private void generateId() {
        if (this.audioId == null) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            this.audioId = "vlk_aud" + uuid.substring(0, 6);
        }
    }
}
