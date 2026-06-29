package com.example.wordcraft.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "test_results")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_id", nullable = false)
    private Vocabularies vocabulary;

    @Column(nullable = false)
    private Integer score; // 점수 (0~100)

    @Column(columnDefinition = "TEXT")
    private String wrongWords; // 틀린 단어 목록 (JSON 문자열)

    @Column(updatable = false)
    private LocalDateTime takenAt;

    @PrePersist
    public void prePersist() {
        this.takenAt = LocalDateTime.now();
    }
}