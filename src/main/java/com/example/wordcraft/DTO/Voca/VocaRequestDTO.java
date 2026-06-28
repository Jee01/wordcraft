package com.example.wordcraft.DTO.Voca;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VocaRequestDTO {
    @NotBlank(message = "title")
    private String title;
    @NotBlank(message = "tag")
    private String tag;
    @NotNull(message = "isPublic")
    private Boolean isPublic;
    private List<VocaWordRequestDTO> words;
}
