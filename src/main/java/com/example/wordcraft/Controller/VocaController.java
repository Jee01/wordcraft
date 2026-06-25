package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.Voca.*;
import com.example.wordcraft.Service.VocaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vocab")
@RequiredArgsConstructor
public class VocaController {
    private final VocaService vocaService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createVoca(@Valid @RequestBody VocaCreateRequestDTO vocaCreateRequestDTO
    ,@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        vocaService.createVocabularies(vocaCreateRequestDTO, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "success createVocabularies"));
    }

    @GetMapping("/my")
    public ResponseEntity<List<VocaResponseDTO>> getMyVocaList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<VocaResponseDTO> vocabularies = vocaService.getVocaListByUserId(email);
        return ResponseEntity.ok(vocabularies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocaDetailResponseDTO> getVocaById(@AuthenticationPrincipal UserDetails userDetails
            ,@PathVariable Long id) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(vocaService.getVocaDetail(email, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateVoca(@PathVariable Long id, @RequestBody VocabUpdateDTO vocabUpdateDTO) {
        vocaService.updateVoca(id, vocabUpdateDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success updateVocabularies"));
    }

    @PutMapping("/{id}/wordLearn")
    public ResponseEntity<Map<String, String>> updateLearned(@PathVariable Long id,
                                                             @RequestBody VocaWordLearnDTO vocaWordLearnDTO) {
        vocaService.updateVocaWordLearn(id, vocaWordLearnDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success updateLearnedWord"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoca(@PathVariable Long id) {
        vocaService.deleteVoca(id);
        return ResponseEntity.noContent().build();
    }
}
