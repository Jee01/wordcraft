package com.example.wordcraft.DTO.Login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDeleteDTO {
    @NotBlank
    private String password;
}
