package com.example.wordcraft.DTO;

import com.example.wordcraft.Entity.VocaWords;
import com.example.wordcraft.Entity.Vocabularies;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class VocaDetailResponseDTO {
    private Vocabularies vocabularies;
    private List<VocaWords> words;
}
