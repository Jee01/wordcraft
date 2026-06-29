package com.example.wordcraft.Entity.Voca;

import com.example.wordcraft.Entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="vocabularies")
@Builder
public class Vocabularies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orginId")
    private Long originId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 50)
    private String tag;

    @Column(nullable = false) //비공개 여부
    private Boolean isPublic;

    @Column(nullable = false) //작성 시간
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocaWords> words = new ArrayList<>();

    @PrePersist //DB 저장 직전에 자동 실행
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
