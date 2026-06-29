package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.Test.*;
import com.example.wordcraft.Service.Test.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;

    // 뜻 맞추기 문제 조회
    @GetMapping("/{vocabId}/multiple-choice")
    public ResponseEntity<List<MultipleChoiceQuestionDTO>> getMultipleChoice(
            @PathVariable Long vocabId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(testService.getMultipleChoiceQuestions(vocabId, getEmail(userDetails)));
    }

    // 빈칸 채우기 문제 조회
    @GetMapping("/{vocabId}/fill-blank")
    public ResponseEntity<List<FillBlankQuestionDTO>> getFillBlank(
            @PathVariable Long vocabId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(testService.getFillBlankQuestions(vocabId, getEmail(userDetails)));
    }

    // 테스트 결과 저장
    @PostMapping("/result")
    public ResponseEntity<Map<String, String>> saveResult(
            @RequestBody TestResultRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        testService.saveTestResult(request, getEmail(userDetails));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "success saveTestResult"));
    }

    // 대시보드 최근 학습 활동
    @GetMapping("/recent")
    public ResponseEntity<List<RecentActivityDTO>> getRecentActivities(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(testService.getRecentActivities(getEmail(userDetails)));
    }

    private String getEmail(UserDetails userDetails) {
        return userDetails.getUsername();
    }
}
