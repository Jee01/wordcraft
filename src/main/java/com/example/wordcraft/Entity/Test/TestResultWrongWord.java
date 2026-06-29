package com.example.wordcraft.Entity.Test;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "test_result_wrong_words")
public class TestResultWrongWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_result_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TestResult testResult;

    private Long wordId; // 원본 VocaWords 참조용 (nullable)

    @Column(nullable = false, length = 100)
    private String word;

    @Column(length = 100)
    private String ipa;

    @Column(nullable = false)
    private Integer wrongCount; // 해당 단어를 틀린 횟수

    @OneToMany(mappedBy = "wrongWord", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestResultWrongWordDetail> details = new ArrayList<>();
}
