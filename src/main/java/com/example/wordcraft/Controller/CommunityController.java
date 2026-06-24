package com.example.wordcraft.Controller;

import com.example.wordcraft.Service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @GetMapping("{id}")
    public String copyVocabularies(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        communityService.copyVocabularies(id, email);
        return null;
    }
}
