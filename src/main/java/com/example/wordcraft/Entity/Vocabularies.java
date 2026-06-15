package com.example.wordcraft.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="vocabularies")
@Builder
public class Vocabularies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 50)
    private String tag;

    @Column(nullable = false) //비공개 여부
    private boolean isPublic;

    @Column(nullable = false) //작성 시간
    private LocalDateTime createdAt;

    @PrePersist //DB 저장 직전에 자동 실행
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
