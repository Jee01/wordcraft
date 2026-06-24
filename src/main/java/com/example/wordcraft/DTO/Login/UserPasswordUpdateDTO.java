package com.example.wordcraft.DTO.Login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPasswordUpdateDTO {
    @NotBlank
    private String password;
    @NotBlank
    private String updatePassword;
}
