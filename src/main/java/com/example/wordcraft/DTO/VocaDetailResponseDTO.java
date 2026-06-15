package com.example.wordcraft.DTO;

import com.example.wordcraft.Entity.VocaWords;
import com.example.wordcraft.Entity.Vocabularies;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class VocaDetailResponseDTO {
    private Long id;
    private String title;
    private String description;
    private List<String> tags;       // tag 콤마 문자열을 split해서 배열로
    private boolean isPublic;
    private int wordCount;
    private int learnedCount;
    private String updatedAt;        // "2026-06-06" 형태 (앞 10자)
    private String author;           // nickname
    private List<VocaWordRequestDTO> words;

}
