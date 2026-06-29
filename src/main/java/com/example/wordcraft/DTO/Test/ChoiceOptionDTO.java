package com.example.wordcraft.DTO.Test;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChoiceOptionDTO {
    private Long detailId;
    private String pos;
    private String meaning;
}
