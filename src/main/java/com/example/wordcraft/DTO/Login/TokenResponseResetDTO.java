package com.example.wordcraft.DTO.Login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseResetDTO {
    private String accessToken;
}
