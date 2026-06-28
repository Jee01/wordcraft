package com.example.wordcraft.Entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name="voca_word_detail")
@Builder
public class VocaWordDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocaw_id", nullable = false)
    private VocaWords vocaWords;

    @Column(length = 50)
    private String pos; // 품사

    @Column(columnDefinition = "TEXT")
    private String meanings; // JSON 문자열로 저장

    @Column(columnDefinition = "TEXT")
    private String examples; // JSON 문자열로 저장
}
