package com.example.wordcraft.DTO.Test;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FillBlankQuestionDTO {
    private Long wordId;
    private String word;
    private Long detailId;
    private String pos;
    private String meaning;
    private String example; // 단어가 ___로 대체된 예문
}
