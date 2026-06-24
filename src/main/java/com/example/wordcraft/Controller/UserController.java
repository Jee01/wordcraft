package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.Login.*;
import com.example.wordcraft.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "success registered"));
    }
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login (@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        TokenResponseDTO tokenResponseDTO = userService.login(loginRequestDTO);
        return ResponseEntity.ok(tokenResponseDTO);
    }

    @PutMapping("/update-password")
    public ResponseEntity<Map<String, String>> updatePassword (@Valid @RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO,
                                                       @AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        userService.updateUserPassword(email, userPasswordUpdateDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success update password"));
    }

    @PutMapping("/update-nickname")
    public ResponseEntity<Map<String, String>> updateNickname (@Valid @RequestBody UserNicknameUpdateDTO userNicknameUpdateDTO,
                                                               @AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        userService.updateUserNickname(email, userNicknameUpdateDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success update nickname"));

    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, String>> delete (@Valid @RequestBody UserDeleteDTO userDeleteDTO,
                                                       @AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        userService.deleteUser(email, userDeleteDTO);
        return ResponseEntity.ok(Map.of("message", "success delete"));
    }
}
