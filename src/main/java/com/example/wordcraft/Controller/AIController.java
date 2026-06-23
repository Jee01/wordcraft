package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.AI.GenerateWordRequestDTO;
import com.example.wordcraft.DTO.AI.WordAnalysisDTO;
import com.example.wordcraft.Service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping(value = "/analyze-file", consumes = "multipart/form-data")
    public ResponseEntity<List<WordAnalysisDTO>> analyzeFile(
            @RequestHeader("X-AI-Api-Key") String apiKey,
            @RequestParam("file") MultipartFile file,
            @RequestParam("tag") String tag
    ) throws IOException {
        List<WordAnalysisDTO> result = geminiService.fileAnalyzeResponse(file, apiKey, tag);
        return ResponseEntity.ok(result);
    }
}
