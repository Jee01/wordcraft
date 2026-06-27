package com.example.wordcraft.Service.UserService;

import com.example.wordcraft.DTO.Login.*;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.JWT.JwtTokenProvider;
import com.example.wordcraft.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /*회원 가입*/
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        if(userRepository.findByEmail(userRegisterDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("already registered E-mail");
        }
        Users users = new Users();
        users.setNickname(userRegisterDTO.getNickname());
        users.setEmail(userRegisterDTO.getEmail());
        users.setProvider("local");

        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        users.setPassword(encodedPassword);

        userRepository.save(users);
    }
    /*회원 로그인*/
    @Transactional
    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Users users = validUser(loginRequestDTO.getEmail());
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

    /*회원 비밀번호 수정*/
    @Transactional
    public void updateUserPassword(String email, UserPasswordUpdateDTO userPasswordUpdateDTO){
        Users users = validUser(email);

        if(!passwordEncoder.matches(userPasswordUpdateDTO.getPassword(), users.getPassword())) {
            throw new IllegalStateException("wrong password");
        }

        users.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.getUpdatePassword()));
        userRepository.save(users);
    }

    @Transactional
    public void updateUserNickname(String email, UserNicknameUpdateDTO userNicknameUpdateDTO) {
        Users users = validUser(email);
        users.setNickname(userNicknameUpdateDTO.getUpdateNickname());
        userRepository.save(users);
    }

    /*회원 삭제*/
    @Transactional
    public void deleteUser(String email, UserDeleteDTO userDeleteDTO) {
        Users users = validUser(email);
        if(!passwordEncoder.matches(userDeleteDTO.getPassword(), users.getPassword())) {
            throw new IllegalStateException("wrong password");
        }
        userRepository.delete(users);
    }

    @Transactional
    public void logout(String email) {
        Users users = validUser(email);
        users.setRefreshToken(null);
        userRepository.save(users);
    }

    public String refreshToken(String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalStateException("invalid or expired token");
        }
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Users users = validUser(email);
        if(!refreshToken.equals(users.getRefreshToken())) {
            throw new IllegalStateException("refresh token does not match");
        }

        return jwtTokenProvider.generateAccessToken(email);
    }

    public UserDTO loginValidate(UserDetails userDetails) {
        Users users = validUser(userDetails.getUsername());

        return UserDTO.builder()
                .email(users.getEmail())
                .nickname(users.getNickname())
                .build();
    }

    private Users validUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("email not found"));
    }
}
