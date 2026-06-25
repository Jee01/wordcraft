package com.example.wordcraft.DTO.Voca;

import lombok.Data;

@Data
public class VocawUpdateDTO {
    private Long id;
    private String word;
    private String ipa;
    private String pos;
    private String meanings;
    private String examples;
    private String memoryTip;
    private Boolean learned;
}
