package com.example.wordcraft.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VocaCreateRequestDTO {
    @NotBlank(message = "title")
    private String title;
    @NotBlank(message = "tag")
    private String tag;
    @NotBlank(message = "isPublic")
    private Boolean isPublic;
    private List<VocaWordRequestDTO> words;
}
