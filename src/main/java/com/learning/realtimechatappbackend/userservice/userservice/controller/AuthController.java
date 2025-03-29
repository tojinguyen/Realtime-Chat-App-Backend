package com.learning.userservice.userservice.controller;

import com.learning.userservice.userservice.dto.request.*;
import com.learning.userservice.userservice.dto.response.*;
import com.learning.userservice.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@Slf4j
@AllArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;

    //Region:  Register
    @PostMapping("/send-register-code")
    public ResponseEntity<ApiResponse<SendRegisterVerifyCodeResponse>> sendVerificationCode(
            @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.sendRegisterVerificationCode(registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SendRegisterVerifyCodeResponse>builder()
                    .success(true)
                    .message("Verification code sent successfully")
                    .data(new SendRegisterVerifyCodeResponse("Verification code sent to " + registerRequest.getEmail()))
                    .build());
        } catch (ResponseStatusException e) {
            log.error("Failed to send verification code: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<SendRegisterVerifyCodeResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error when sending verification code: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<SendRegisterVerifyCodeResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build());
        }
    }

    @PostMapping("/verify-register-code")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verify(
            @Valid @RequestBody VerifyRegisterCodeRequest verifyRegisterCodeRequest) {
        try {
            return ResponseEntity.ok(authService.verifyAndCreateUser(verifyRegisterCodeRequest));
        } catch (ResponseStatusException e) {
            log.error("Verification failed: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build());
        }
    }
    //EndRegion

    //Region:  Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.login(loginRequest));
        } catch (ResponseStatusException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build());
        }
    }
    //EndRegion

    //Region:  Reset Password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest email) {
        try {
            authService.sendResetPasswordVerificationCode(email.getEmail());
            return ResponseEntity.ok(ApiResponse.<ForgotPasswordResponse>builder()
                    .success(true)
                    .message("Verification code sent successfully")
                    .data(new ForgotPasswordResponse("Verification code sent to " + email.getEmail()))
                    .build());
        } catch (ResponseStatusException e) {
            log.error("Failed to send reset password code: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<ForgotPasswordResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error when sending reset password code: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<ForgotPasswordResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            var response = authService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            log.error("Reset password failed: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<ResetPasswordResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during password reset: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<ResetPasswordResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build());
        }
    }
    //EndRegion

    //Region:  Logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(
            @RequestHeader("Authorization") String token) {
        try {
            authService.logout(token);
            return ResponseEntity.ok(ApiResponse.<LogoutResponse>builder()
                    .success(true)
                    .message("Logout successful")
                    .data(new LogoutResponse("Logout successful"))
                    .build());
        } catch (ResponseStatusException e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<LogoutResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during logout: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<LogoutResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build());
        }
    }
    //EndRegion

    //Region: Refresh Token
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest.getRefreshToken()));
        } catch (ResponseStatusException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build());
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build());
        }
    }
    //EndRegion

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth service is up and running");
    }
}
