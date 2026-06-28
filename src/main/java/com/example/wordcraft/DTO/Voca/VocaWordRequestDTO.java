package com.example.wordcraft.DTO.Voca;

import lombok.Data;

import java.util.List;

@Data
public class VocaWordRequestDTO {
    private Long id;
    private String word;
    private List<VocaWordDetailDTO> vocaWordDetailDTOS; //뜻, 품사, 예문
    private String ipa;
    private String memoryTip;
    private Boolean learned;
}
