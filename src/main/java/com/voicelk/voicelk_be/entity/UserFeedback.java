package com.voicelk.voicelk_be.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@ToString(exclude = {"registeredUser", "audio"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_feedbacks")
public class UserFeedback {

    @Id
    @Column(name = "feedback_id", nullable = false, updatable = false, unique = true)
    private String feedbackId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private RegisteredUser registeredUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_id", nullable = false, unique = true)
    private Audio audio;

    @PrePersist
    private void onCreate() {
        if (this.feedbackId == null) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            this.feedbackId = "vlk_fb" + uuid.substring(0, 6);
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
