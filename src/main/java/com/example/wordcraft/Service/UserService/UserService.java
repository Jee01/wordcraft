package com.example.wordcraft.Service.UserService;

import com.example.wordcraft.DTO.Login.*;
import com.example.wordcraft.DTO.Mail.EmailCodeVerifyDTO;
import com.example.wordcraft.DTO.Mail.ForgotPasswordUpdateDTO;
import com.example.wordcraft.DTO.User.*;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Exception.DuplicateException;
import com.example.wordcraft.Exception.InvalidTokenException;
import com.example.wordcraft.Exception.ResourceNotFoundException;
import com.example.wordcraft.Exception.UnauthorizedException;
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
            throw new DuplicateException("already registered");
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
            throw new UnauthorizedException("email or password is incorrect.");
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
            throw new UnauthorizedException("password is incorrect.");
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
        if (!"google".equals(users.getProvider())) {
            if (userDeleteDTO.getPassword() == null ||
                !passwordEncoder.matches(userDeleteDTO.getPassword(), users.getPassword())) {
                throw new UnauthorizedException("비밀번호가 올바르지 않습니다.");
            }
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
            throw new InvalidTokenException("expired token");
        }
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        Users users = validUser(email);
        if(!refreshToken.equals(users.getRefreshToken())) {
            throw new InvalidTokenException("token is incorrect.");
        }

        return jwtTokenProvider.generateAccessToken(email);
    }

    public UserDTO loginValidate(UserDetails userDetails) {
        Users users = validUser(userDetails.getUsername());

        return UserDTO.builder()
                .email(users.getEmail())
                .nickname(users.getNickname())
                .provider(users.getProvider())
                .build();
    }

    /*비밀번호 재설정 토큰 발급*/
    public TokenResponseResetDTO ResetPasswordToken(String email) {
        validUser(email);

        String accessToken = jwtTokenProvider.generateAccessToken(email);

        return TokenResponseResetDTO.builder()
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        Users users = validUser(email);
        users.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(users);
    }


    public Boolean isValidEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private Users validUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("not found"));
    }
}
