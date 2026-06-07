package com.example.wordcraft.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @Email(message = "not email form")
    private String email;

    @NotBlank(message = "password is essential")
    private String password;
}
