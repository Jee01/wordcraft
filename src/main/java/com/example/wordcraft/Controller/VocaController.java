package com.example.wordcraft.Controller;

import com.example.wordcraft.Service.VocaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/voca")
public class VocaController {
    private final VocaService vocaService;

    public VocaController(VocaService vocaService) {this.vocaService = vocaService;}

    @GetMapping("/createVoca") //반환 타입 이후 변경
    public ResponseEntity<Map<String, String>> createVoca() {

        return null;
    }
}
