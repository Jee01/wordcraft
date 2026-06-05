package com.example.wordcraft.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="voca_words")
@Builder
public class VocaWords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_id", nullable = false)
    private Vocabularies vocabulary;

    @Column(nullable = false, length = 100)
    private String word;

    @Column(length = 100)
    private String ipa; // 발음기호

    @Column(length = 50)
    private String pos; // 품사

    @Column(columnDefinition = "TEXT")
    private String meanings; // JSON 문자열로 저장

    @Column(columnDefinition = "TEXT")
    private String examples; // JSON 문자열로 저장

    @Column(columnDefinition = "TEXT")
    private String memoryTip;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
