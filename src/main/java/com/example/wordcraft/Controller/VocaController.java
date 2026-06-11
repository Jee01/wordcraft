package com.example.wordcraft.Controller;

import com.example.wordcraft.Service.VocaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vocab")
public class VocaController {
    private final VocaService vocaService;

    public VocaController(VocaService vocaService) {this.vocaService = vocaService;}

    @PostMapping //반환 타입 이후 변경
    public ResponseEntity<Map<String, String>> createVoca() {

        return null;
    }
    @GetMapping
    public ResponseEntity<Map<String, String>> getVocaList() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getVocaById(@PathVariable String id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateVoca(@PathVariable String id, @RequestBody Map<String, String> voca) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVoca(@PathVariable String id) {
        return null;
    }
}
