package com.example.wordcraft.Service;

import com.example.wordcraft.DTO.Login.LoginRequestDTO;
import com.example.wordcraft.DTO.Login.TokenResponseDTO;
import com.example.wordcraft.DTO.Login.UserRegisterDTO;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.JWT.JwtTokenProvider;
import com.example.wordcraft.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Users users = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalStateException("email not found"));
        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), users.getPassword())) {
            throw new IllegalStateException("wrong password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(loginRequestDTO.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequestDTO.getEmail());

        users.setRefreshToken(refreshToken);
        userRepository.save(users);

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    /*회원 수정*/
    /*회원 삭제*/
}
