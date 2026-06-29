package com.example.wordcraft.Entity.Test;

import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.Voca.Vocabularies;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Vocabularies vocabulary;

    @Column(nullable = false)
    private String testType;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer wrongCount;

    @OneToMany(mappedBy = "testResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestResultWrongWord> wrongWords = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime takenAt;

    @PrePersist
    public void prePersist() {
        this.takenAt = LocalDateTime.now();
    }
}