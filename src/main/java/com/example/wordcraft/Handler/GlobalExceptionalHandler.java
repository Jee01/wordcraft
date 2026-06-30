package com.example.wordcraft.Handler;

import com.example.wordcraft.Exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionalHandler {

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(Map.of("code", "DUPLICATE", "message", e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(Map.of("code", "NOT_FOUND", "message", e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(Map.of("code", "FORBIDDEN", "message", e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(Map.of("code", "INVALID_TOKEN", "message", e.getMessage()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Map<String, String>> handleExternalApi(ExternalApiException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY) // 502
                .body(Map.of("code", "EXTERNAL_API_ERROR", "message", e.getMessage()));
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<Map<String, String>> handleEmailSend(EmailSendException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(Map.of("code", "EMAIL_SEND_FAILED", "message", e.getMessage()));
    }

    // 최후 방어선 — 처리되지 않은 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body(Map.of("code", "INTERNAL_ERROR", "message", "서버 오류가 발생했습니다."));
    }
}
