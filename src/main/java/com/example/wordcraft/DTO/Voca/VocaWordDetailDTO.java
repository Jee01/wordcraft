package com.example.wordcraft.DTO.Voca;

import lombok.Data;

import java.util.List;

@Data
public class VocaWordDetailDTO {
    private Long id;
    private String meaning;
    private String pos;
    private String examples;
}
