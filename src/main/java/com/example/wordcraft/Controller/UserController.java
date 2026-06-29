package com.example.wordcraft.Controller;

import com.example.wordcraft.DTO.Login.*;
import com.example.wordcraft.DTO.Mail.EmailCodeVerifyDTO;
import com.example.wordcraft.DTO.Mail.EmailVerifyRequestDTO;
import com.example.wordcraft.DTO.Mail.ForgotPasswordUpdateDTO;
import com.example.wordcraft.DTO.User.*;
import com.example.wordcraft.Service.UserService.EmailService;
import com.example.wordcraft.Service.UserService.UserService;
import com.example.wordcraft.Util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    //만료 15분
    private static final int ACCESS_TOKEN_EXPIRE_SECONDS = 60 * 15;
    //만료 7일
    private static final int REFRESH_TOKEN_EXPIRE_SECONDS = 60 * 60 * 24 * 7;

    //만료 5분
    private static final int ACCESS_TOKEN_FOR_RESET_PASSWORD = 60 * 5;

    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody EmailVerifyRequestDTO emailVerifyRequestDTO){
        emailService.sendEmail(emailVerifyRequestDTO);
        return ResponseEntity.ok(Map.of("message", "success send email"));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Boolean> verifyEmail(@Valid @RequestBody EmailCodeVerifyDTO emailCodeVerifyDTO){
        Boolean verify = emailService.verifyCode(emailCodeVerifyDTO);
        return ResponseEntity.ok(verify);
    }

    @PostMapping("/email/forgotPassword")
    public ResponseEntity<Map<String, String>> sendResetEmail(@Valid @RequestBody EmailVerifyRequestDTO emailVerifyRequestDTO){
        if(userService.isValidEmail(emailVerifyRequestDTO.getEmail())){
            emailService.sendEmail(emailVerifyRequestDTO);
        }
        return ResponseEntity.ok(Map.of("message", "success send email"));
    }

    @PostMapping("/email/verify/forgotPassword")
    public ResponseEntity<Boolean> verifyResetEmail(@Valid @RequestBody EmailCodeVerifyDTO emailCodeVerifyDTO, HttpServletResponse response){
        Boolean verify = emailService.verifyCode(emailCodeVerifyDTO);
        if(verify){
            TokenResponseResetDTO tokenResponseResetDTO = userService.ResetPasswordToken(emailCodeVerifyDTO.getEmail());
            CookieUtil.addTokenCookie(response,"access_token",tokenResponseResetDTO.getAccessToken(),ACCESS_TOKEN_FOR_RESET_PASSWORD);
        }
        return ResponseEntity.ok(verify);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ForgotPasswordUpdateDTO forgotPasswordUpdateDTO,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        userService.resetPassword(getEmail(userDetails), forgotPasswordUpdateDTO.getNewPassword());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success update password"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "success registered"));
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login (@Valid @RequestBody LoginRequestDTO loginRequestDTO,
                                                   HttpServletResponse response){

        TokenResponseDTO tokenResponseDTO = userService.login(loginRequestDTO);
        CookieUtil.addTokenCookie(response,"access_token",tokenResponseDTO.getAccessToken(),ACCESS_TOKEN_EXPIRE_SECONDS);
        CookieUtil.addTokenCookie(response,"refresh_token",tokenResponseDTO.getRefreshToken(),REFRESH_TOKEN_EXPIRE_SECONDS);
        return ResponseEntity.ok(Map.of("message", "success login"));
    }

    @PutMapping("/update-password")
    public ResponseEntity<Map<String, String>> updatePassword (@Valid @RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO,
                                                       @AuthenticationPrincipal UserDetails userDetails){

        userService.updateUserPassword(getEmail(userDetails), userPasswordUpdateDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success update password"));
    }

    @PutMapping("/update-nickname")
    public ResponseEntity<Map<String, String>> updateNickname (@Valid @RequestBody UserNicknameUpdateDTO userNicknameUpdateDTO,
                                                               @AuthenticationPrincipal UserDetails userDetails){

        userService.updateUserNickname(getEmail(userDetails), userNicknameUpdateDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "success update nickname"));

    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, String>> delete (@Valid @RequestBody UserDeleteDTO userDeleteDTO,
                                                       @AuthenticationPrincipal UserDetails userDetails){

        userService.deleteUser(getEmail(userDetails), userDeleteDTO);
        return ResponseEntity.ok(Map.of("message", "success delete"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal UserDetails userDetails,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response){
        userService.logout(getEmail(userDetails));

        // Google 로그인 쿠키 삭제
        CookieUtil.removeCookie(request, response, "access_token");
        CookieUtil.removeCookie(request, response, "refresh_token");

        return ResponseEntity.ok(Map.of("message", "success logout"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request,
                                                       HttpServletResponse response) {
        // Body 대신 쿠키에서 refreshToken 읽기
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "refresh token not found"));
        }

        String newAccessToken = userService.refreshToken(refreshToken);

        // 새 accessToken을 쿠키로 반환
        CookieUtil.addTokenCookie(response, "access_token", newAccessToken, ACCESS_TOKEN_EXPIRE_SECONDS);

        return ResponseEntity.ok(Map.of("message", "token refreshed"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.loginValidate(userDetails));
    }

    private String getEmail(UserDetails userDetails){
        return userDetails.getUsername();
    }
}
