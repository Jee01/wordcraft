package com.example.wordcraft.Entity.Test;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "test_result_wrong_word_details")
public class TestResultWrongWordDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wrong_word_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TestResultWrongWord wrongWord;

    @Column(length = 50)
    private String pos;

    @Column(columnDefinition = "TEXT")
    private String meanings;

    @Column(columnDefinition = "TEXT")
    private String examples;
}
