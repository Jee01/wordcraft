package com.example.wordcraft.DTO.Test;

import com.example.wordcraft.Entity.Test.TestResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentActivityDTO {
    private Long testResultId;
    private String vocabTitle;
    private String testType;
    private String takenAt;
    private Integer score;

    public static RecentActivityDTO from(TestResult result) {
        return RecentActivityDTO.builder()
                .testResultId(result.getId())
                .vocabTitle(result.getVocabulary().getTitle())
                .testType(result.getTestType())
                .takenAt(result.getTakenAt().toString().substring(0, 10))
                .score(result.getScore())
                .build();
    }
}
