package com.example.wordcraft.DTO.AI;

import lombok.Data;

@Data
public class GenerateWordRequestDTO {
    private String text;
    private String tag;
}
