package com.example.wordcraft.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    //만료 15분
    private static final long ACCESS_TOKEN_EXPIRE_SECONDS = 1000 * 60 * 15;
    //만료 7일
    private static final long REFRESH_TOKEN_EXPIRE_SECONDS = 1000 * 60 * 60 * 24 * 7;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //AccessToken 생성
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRE_SECONDS))
                .signWith(key)
                .compact();
    }

    //RefreshToken 생성
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+REFRESH_TOKEN_EXPIRE_SECONDS))
                .signWith(key)
                .compact();
    }

    // Token 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //Token 유효성
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e){
            log.error("Unsupported JWT token: {}", e.getMessage());
        }
        catch (MalformedJwtException e){
            log.error("Malformed JWT token: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
        }
        return false;
    }

}
