package com.example.wordcraft.Service;

import com.example.wordcraft.DTO.UserDTO;
import com.example.wordcraft.DTO.UserRegisterDTO;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*회원 가입*/
    public void register(UserRegisterDTO userRegisterDTO) {
        if(userRepository.findByEmail(userRegisterDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("already registered E-mail");
        }
        Users users = new Users();
        users.setNickname(userRegisterDTO.getNickname());
        users.setEmail(userRegisterDTO.getEmail());

        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        users.setPassword(encodedPassword);

        userRepository.save(users);
    }
    /*회원 로그인*/
    /*회원 수정*/
    /*회원 삭제*/
}
