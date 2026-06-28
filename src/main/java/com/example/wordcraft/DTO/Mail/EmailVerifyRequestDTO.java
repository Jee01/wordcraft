package com.example.wordcraft.DTO.Mail;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailVerifyRequestDTO {
    @Email
    private String email;
}
