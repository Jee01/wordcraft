package com.example.wordcraft.DTO.Voca;

import lombok.Builder;
import lombok.Getter;


import java.util.List;

@Getter
@Builder
public class VocaDetailResponseDTO {
    private Long id;
    private String title;
    private String description;
    private List<String> tags;       // tag 콤마 문자열을 split 배열로
    private boolean isPublic;
    private int wordCount;
    private int learnedCount;
    private String updatedAt;        // "2026-06-06" 형태 (앞 10자)
    private String author;           // nickname
    private List<VocaWordRequestDTO> words;

}
