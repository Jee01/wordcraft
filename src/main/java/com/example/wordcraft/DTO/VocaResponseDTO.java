package com.example.wordcraft.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class VocaResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private String tag;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private Integer wordCount;
}