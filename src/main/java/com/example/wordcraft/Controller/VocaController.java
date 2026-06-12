package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.VocaCreateRequestDTO;
import com.example.wordcraft.DTO.VocaResponseDTO;
import com.example.wordcraft.Service.VocaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vocab")
public class VocaController {
    private final VocaService vocaService;

    public VocaController(VocaService vocaService) {this.vocaService = vocaService;}

    @PostMapping //반환 타입 이후 변경
    public ResponseEntity<VocaResponseDTO> createVoca(@Valid @RequestBody VocaCreateRequestDTO vocaCreateRequestDTO) {

        return null;
    }
    @GetMapping
    public ResponseEntity<List<VocaResponseDTO>> getVocaList() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getVocaById(@PathVariable String id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<VocaResponseDTO> updateVoca(@PathVariable String id, @RequestBody Map<String, String> voca) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoca(@PathVariable String id) {
        return null;
    }
}
