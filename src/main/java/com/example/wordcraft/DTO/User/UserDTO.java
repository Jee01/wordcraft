package com.example.wordcraft.DTO.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;

    private String nickname;

    private String email;
}
