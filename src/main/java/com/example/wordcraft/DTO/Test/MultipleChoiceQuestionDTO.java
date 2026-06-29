package com.example.wordcraft.DTO.Test;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MultipleChoiceQuestionDTO {
    private Long wordId;
    private String word;
    private String ipa;
    private Long correctDetailId;
    private List<ChoiceOptionDTO> options;
}
