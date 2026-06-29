package com.example.wordcraft.DTO.AI;

import lombok.Data;

import java.util.List;

@Data
public class WordAnalysisDTO {
    private String word;
    private String ipa;
    private String memoryTip;
    private List<DetailDTO> details;

    @Data
    public static class DetailDTO {
        private String pos;
        private List<String> meanings;
        private List<ExampleDTO> examples;
    }

    @Data
    public static class ExampleDTO {
        private String en;
        private String ko;
    }
}
