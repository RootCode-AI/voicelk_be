package com.voicelk.voicelk_be.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
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
@ToString(exclude = {"user", "answer"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "queries")
public class Query {

    @Id
    @Column(name = "query_id", nullable = false, updatable = false, unique = true)
    private String queryId;

    @Column(name = "input_text", nullable = false, columnDefinition = "TEXT")
    private String inputText;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "syllabus_topic")
    private String syllabusTopic;

    // Many-to-One: Many queries can be submitted by one User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // One-to-One: One query generates one Answer
    @OneToOne(mappedBy = "query", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Answer answer;

    @PrePersist
    private void onCreate() {
        if (this.queryId == null) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            this.queryId = "vlk_qry" + uuid.substring(0, 6);
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
