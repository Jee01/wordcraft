package com.example.wordcraft.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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

    @Column(columnDefinition = "TEXT")
    private String memoryTip;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean learned;

    @OneToMany(mappedBy = "vocaWords", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocaWordDetail> details = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
