package com.example.wordcraft.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VocaResponseDTO {
    @NotBlank(message = "id")
    private Long id;
    @NotBlank(message = "userId")
    private Long userId;
    @NotBlank(message = "title")
    private String title;
    @NotBlank(message = "tag")
    private String tag;
    @NotBlank(message = "isPublic")
    private Boolean isPublic;
    @NotBlank(message = "crateAt")
    private LocalDateTime createAt;
    @NotBlank(message = "wordCount")
    private Integer wordCount;
}
