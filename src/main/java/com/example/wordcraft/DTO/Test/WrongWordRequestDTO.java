package com.example.wordcraft.DTO.Test;

import lombok.Data;

import java.util.List;

@Data
public class WrongWordRequestDTO {
    private Long wordId;
    private String word;
    private String ipa;
    private Integer wrongCount;
    private List<WrongWordDetailRequestDTO> details;
}
