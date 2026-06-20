package com.example.wordcraft.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @PostMapping("/generate-word")
    public String generateWord() {
        return null;
    }
}
