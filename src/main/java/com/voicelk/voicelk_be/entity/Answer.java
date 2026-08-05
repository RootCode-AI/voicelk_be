package com.voicelk.voicelk_be.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@ToString(exclude = "query")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "answers")
public class Answer {

    @Id
    @Column(name = "answer_id", nullable = false, updatable = false, unique = true)
    private String answerId;

    @Column(name = "response_text", nullable = false, columnDefinition = "TEXT")
    private String responseText;

    @Column(name = "source")
    private String source;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "query_id", nullable = false, unique = true)
    private Query query;

    @PrePersist
    private void generateId() {
        if (this.answerId == null) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            this.answerId = "vlk_ans" + uuid.substring(0, 6);
        }
    }
}
