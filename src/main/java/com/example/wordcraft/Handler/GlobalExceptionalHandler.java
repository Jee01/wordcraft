package com.example.wordcraft.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

//예외 처리
@RestControllerAdvice
public class GlobalExceptionalHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleISE(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) //409
                .body(Map.of(
                        "code", "EMAIL_DUPLICATE", //이메일 중복
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(Map.of(
                        "code", "LOGIN_FAILED",
                        "message", e.getMessage()
                ));
    }
}
