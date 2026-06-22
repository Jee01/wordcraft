package com.example.wordcraft.DTO.AI;

import lombok.Data;

import java.util.List;

@Data
public class GenerateWordRequestDTO {
    private List<String> words;
    private String tag;
}
