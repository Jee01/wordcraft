package com.example.wordcraft.DTO.Voca;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class VocabUpdateDTO {
    private String title;
    private String tag;
    private Boolean isPublic;
    private LocalDateTime updateAt;
    private List<VocawUpdateDTO> words;

    @PrePersist //DB 저장 직전에 자동 실행
    public void prePersist() {
        this.updateAt = LocalDateTime.now();
    }

}