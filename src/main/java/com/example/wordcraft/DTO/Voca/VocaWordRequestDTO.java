package com.example.wordcraft.DTO.Voca;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class VocaWordRequestDTO {
    private Long id;
    @NotBlank(message = "word")
    private String word;
    @NotBlank(message = "meaning")
    private String meaning;
    private String pos;
    private String ipa;
    private String examples;
    private String memoryTip;
    private Boolean learned;
}
