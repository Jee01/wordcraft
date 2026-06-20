package com.example.wordcraft.DTO.Voca;

import com.example.wordcraft.Entity.Vocabularies;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VocawUpdateDTO {
    private String word;
    private String ipa;
    private String pos;
    private String meanings;
    private String examples;
    private String memoryTip;
}
