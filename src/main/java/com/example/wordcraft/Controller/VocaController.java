package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.Voca.*;
import com.example.wordcraft.Service.VocaService.VocaService;
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

    //단어장 생성
    @PostMapping
    public ResponseEntity<Map<String, String>> createVoca(@Valid @RequestBody VocaRequestDTO vocaRequestDTO
    ,@AuthenticationPrincipal UserDetails userDetails) {
        vocaService.createVocabularies(vocaRequestDTO, getEmail(userDetails));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "success createVocabularies"));
    }

    //내가 생성한 단어장 조회
    @GetMapping("/my")
    public ResponseEntity<List<VocaResponseDTO>> getMyVocaList(@AuthenticationPrincipal UserDetails userDetails) {
        List<VocaResponseDTO> vocabularies = vocaService.getVocaListByUserId(getEmail(userDetails));
        return ResponseEntity.ok(vocabularies);
    }

    //단어장 세부 조회
    @GetMapping("/{id}")
    public ResponseEntity<VocaDetailResponseDTO> getVocaById(@AuthenticationPrincipal UserDetails userDetails
            ,@PathVariable Long id) {
        return ResponseEntity.ok(vocaService.getVocaDetail(getEmail(userDetails), id));
    }

    //단어장 수정
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateVoca(@PathVariable Long id, @RequestBody VocaRequestDTO vocabUpdateDTO
            , @AuthenticationPrincipal UserDetails userDetails) {
        vocaService.updateVoca(getEmail(userDetails), id, vocabUpdateDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success updateVocabularies"));
    }

    //단어 학습도 저장
    @PutMapping("/{id}/wordLearn")
    public ResponseEntity<Map<String, String>> updateLearned(@PathVariable Long id,
                                                             @RequestBody VocaWordLearnDTO vocaWordLearnDTO,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        vocaService.updateVocaWordLearn(id, vocaWordLearnDTO, getEmail(userDetails));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success updateLearnedWord"));
    }

    //단어장 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoca(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        vocaService.deleteVoca(id, getEmail(userDetails));
        return ResponseEntity.noContent().build();
    }

    //단어장 전체 삭제
    @DeleteMapping("/my/all")
    public ResponseEntity<Void> deleteAllMyVoca(@AuthenticationPrincipal UserDetails userDetails) {
        vocaService.deleteAllMyVoca(getEmail(userDetails));
        return ResponseEntity.noContent().build();
    }

    private String getEmail(UserDetails userDetails) {
        return userDetails.getUsername();
    }
}
