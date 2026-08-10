package com.voicelk.voicelk_be.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = {"user", "audio"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "download_logs")
public class DownloadLog {

    @Id
    @Column(name = "log_id", nullable = false, updatable = false, unique = true)
    private String logId;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_id", nullable = false)
    private Audio audio;

    @PrePersist
    private void onCreate() {
        if (this.logId == null) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            this.logId = "vlk_log" + uuid.substring(0, 6);
        }
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }
}
