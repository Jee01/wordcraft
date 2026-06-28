package com.example.wordcraft.DTO.Mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailCodeVerifyDTO {
    @Email
    private String email;
    @NotBlank
    private String code;
}
