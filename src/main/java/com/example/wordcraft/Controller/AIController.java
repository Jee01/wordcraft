package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.AI.GenerateWordRequestDTO;
import com.example.wordcraft.DTO.AI.WordAnalysisDTO;
import com.example.wordcraft.Service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final GeminiService geminiService;

    @PostMapping("/generate-word")
    public ResponseEntity<List<WordAnalysisDTO>> generateWord(
            @RequestHeader("X-AI-Api-Key") String apiKey,
            @RequestBody GenerateWordRequestDTO request
    ) {
        List<WordAnalysisDTO> result = geminiService.analyzeWords(
                apiKey, request.getWords(), request.getTag()
        );
        return ResponseEntity.ok(result);
    }
}
