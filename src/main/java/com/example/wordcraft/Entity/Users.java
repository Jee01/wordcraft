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
@Table(name="users")
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String refreshToken;

    @Column
    private String provider; //local or Google

    @Column
    private String providerId;

    @PrePersist //DB 저장 직전에 자동 실행
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
