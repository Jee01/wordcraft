package com.example.wordcraft.DTO.Mail;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordUpdateDTO {
    @NotBlank
    private String newPassword;
}