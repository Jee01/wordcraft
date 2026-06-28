package com.example.wordcraft;

import com.example.wordcraft.DTO.Login.LoginRequestDTO;
import com.example.wordcraft.DTO.Login.TokenResponseDTO;
import com.example.wordcraft.DTO.User.UserRegisterDTO;
import com.example.wordcraft.Service.UserService.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
@Transactional // 테스트 후 DB 롤백
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void registerSuccess() {
        // given
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("test@test.com");
        dto.setNickname("테스트");
        dto.setPassword("test1234");

        // when & then
        assertThatNoException().isThrownBy(() -> userService.register(dto));
    }

    @Test
    @DisplayName("이메일 중복 회원가입 실패")
    void registerDuplicateEmail() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setEmail("test@test.com");
        dto.setNickname("테스트");
        dto.setPassword("test1234");

        userService.register(dto);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("already registered E-mail"); // ← 실제 메시지로 변경
    }

    @Test
    @DisplayName("로그인 성공 및 토큰 발급")
    void loginSuccess() {
        // given
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setEmail("test@test.com");
        registerDTO.setNickname("테스트");
        registerDTO.setPassword("test1234");
        userService.register(registerDTO);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("test@test.com");
        loginDTO.setPassword("test1234");

        // when
        TokenResponseDTO tokenResponse = userService.login(loginDTO);

        // then
        assertThat(tokenResponse.getAccessToken()).isNotNull();
        assertThat(tokenResponse.getRefreshToken()).isNotNull();
        System.out.println("AccessToken: " + tokenResponse.getAccessToken());
        System.out.println("RefreshToken: " + tokenResponse.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void loginFailEmail() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("none@test.com");
        loginDTO.setPassword("test1234");

        assertThatThrownBy(() -> userService.login(loginDTO))
                .isInstanceOf(IllegalStateException.class) // ← IllegalArgumentException → IllegalStateException
                .hasMessage("email not found"); // ← 실제 메시지로 변경
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFailPassword() {
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setEmail("test@test.com");
        registerDTO.setNickname("테스트");
        registerDTO.setPassword("test1234");
        userService.register(registerDTO);

        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setEmail("test@test.com");
        loginDTO.setPassword("wrongpassword");

        assertThatThrownBy(() -> userService.login(loginDTO))
                .isInstanceOf(IllegalStateException.class) // ← IllegalArgumentException → IllegalStateException
                .hasMessage("wrong password"); // ← 실제 메시지로 변경
    }
}