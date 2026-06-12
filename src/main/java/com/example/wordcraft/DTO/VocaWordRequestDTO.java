package com.example.wordcraft.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VocaWordRequestDTO {
    @NotBlank(message = "word")
    private String word;
    @NotBlank(message = "meaning")
    private String meaning;
    private String pos;
    private String ipa;
    private String examples;
    private String memoryTip;
}
