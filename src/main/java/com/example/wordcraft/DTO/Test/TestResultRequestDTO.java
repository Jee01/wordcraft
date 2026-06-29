package com.example.wordcraft.DTO.Test;

import lombok.Data;

import java.util.List;

@Data
public class TestResultRequestDTO {
    private Long vocabId;
    private String testType;
    private Integer totalCount;
    private Integer score;
    private List<WrongWordRequestDTO> wrongWords;
}
