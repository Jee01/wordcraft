package com.example.wordcraft.Handler;

import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Exception.ResourceNotFoundException;
import com.example.wordcraft.JWT.JwtTokenProvider;
import com.example.wordcraft.Repository.UserRepository;
import com.example.wordcraft.Util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    //만료 15분
    private static final int ACCESS_TOKEN_EXPIRE_SECONDS = 60 * 15;
    //만료 7일
    private static final int REFRESH_TOKEN_EXPIRE_SECONDS = 60 * 60 * 24 * 7;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException{
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttribute("email");

        String accessToken = tokenProvider.generateAccessToken(email);
        String refreshToken = tokenProvider.generateRefreshToken(email);

        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("OAuth2 사용자 정보를 찾을 수 없습니다."));
        users.setRefreshToken(refreshToken);
        userRepository.save(users);

        CookieUtil.addTokenCookie(response,"access_token",accessToken, ACCESS_TOKEN_EXPIRE_SECONDS);
        CookieUtil.addTokenCookie(response,"refresh_token",refreshToken, REFRESH_TOKEN_EXPIRE_SECONDS);

        response.sendRedirect("http://localhost:8080/index.html");
    }
}
