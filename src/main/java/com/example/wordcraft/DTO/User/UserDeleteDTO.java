package com.example.wordcraft.DTO.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDeleteDTO {
    @NotBlank
    private String password;
}
