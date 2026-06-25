package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.Voca.VocaDetailResponseDTO;
import com.example.wordcraft.DTO.Voca.VocaResponseDTO;
import com.example.wordcraft.Service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/{id}/copy")
    public ResponseEntity<Map<String, String>> copyVocabularies(@PathVariable Long id,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        communityService.copyVocabularies(id, email);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "success copy vocabularies"));
    }

    @GetMapping
    public ResponseEntity<List<VocaResponseDTO>> getVocaList() {
        List<VocaResponseDTO> vocabularies = communityService.getVocaList();
        return ResponseEntity.ok(vocabularies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocaDetailResponseDTO> getVocaDetail(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getVocaDetail(id));
    }
}
