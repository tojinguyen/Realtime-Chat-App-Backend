package com.learning.userservice.userservice.exception;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.learning.userservice.userservice.dto.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.<String>builder().success(false).message(e.getMessage()).build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(ApiResponse.<String>builder().success(false)
                .message(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage()).build());
    }
}
