package com.example.wordcraft.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
}
