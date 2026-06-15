package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.VocaCreateRequestDTO;
import com.example.wordcraft.DTO.VocaDetailResponseDTO;
import com.example.wordcraft.DTO.VocaResponseDTO;
import com.example.wordcraft.Service.VocaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vocab")
public class VocaController {
    private final VocaService vocaService;

    public VocaController(VocaService vocaService) {this.vocaService = vocaService;}

    @PostMapping
    public ResponseEntity<Map<String, String>> createVoca(@Valid @RequestBody VocaCreateRequestDTO vocaCreateRequestDTO
    ,@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        vocaService.createVocabularies(vocaCreateRequestDTO, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "success createVocabularies"));
    }
    @GetMapping
    public ResponseEntity<List<VocaResponseDTO>> getVocaList() {
        List<VocaResponseDTO> vocabularies = vocaService.getVocaList();
        return ResponseEntity.ok(vocabularies);
    }
    @GetMapping("/my")
    public ResponseEntity<List<VocaResponseDTO>> getMyVocaList(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        List<VocaResponseDTO> vocabularies = vocaService.getVocaListByUserId(email);
        return ResponseEntity.ok(vocabularies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocaDetailResponseDTO> getVocaById(@PathVariable Long id) {
        return ResponseEntity.ok(vocaService.getVocaDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VocaResponseDTO> updateVoca(@PathVariable Long id, @RequestBody Map<String, String> voca) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoca(@PathVariable Long id) {
        vocaService.deleteVoca(id);
        return ResponseEntity.noContent().build();
    }
}
