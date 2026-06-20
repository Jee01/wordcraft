package com.example.wordcraft.DTO.Login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Email(message = "not email form")
    private String email;

    @NotBlank(message = "password is essential")
    private String password;
}
