package com.example.wordcraft.DTO.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDeleteDTO {
    private String password;
}
