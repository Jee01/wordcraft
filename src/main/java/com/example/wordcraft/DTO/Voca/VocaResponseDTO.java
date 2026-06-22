package com.example.wordcraft.DTO.Voca;

import com.example.wordcraft.Entity.Vocabularies;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VocaResponseDTO {
    private Long id;
    private Long userId;
    private String title;
    private String tag;
    private Boolean isPublic;
    private String nickname;
    private LocalDateTime createdAt;
    private Integer wordCount;

    public static VocaResponseDTO from(Vocabularies vocabulary) {
        return VocaResponseDTO.builder()
                .id(vocabulary.getId())
                .userId(vocabulary.getUser().getId())
                .title(vocabulary.getTitle())
                .tag(vocabulary.getTag())
                .isPublic(vocabulary.getIsPublic())
                .nickname(vocabulary.getUser().getNickname())
                .createdAt(vocabulary.getCreatedAt())
                .build();
    }
}