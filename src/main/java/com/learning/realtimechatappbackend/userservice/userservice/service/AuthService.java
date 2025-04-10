package com.learning.realtimechatappbackend.userservice.userservice.service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.learning.realtimechatappbackend.userservice.redisservice.service.BaseRedisService;
import com.learning.realtimechatappbackend.userservice.userservice.dto.request.LoginRequest;
import com.learning.realtimechatappbackend.userservice.userservice.dto.request.ResetPasswordRequest;
import com.learning.realtimechatappbackend.userservice.userservice.dto.request.VerifyRegisterCodeRequest;
import com.learning.realtimechatappbackend.userservice.userservice.dto.response.ApiResponse;
import com.learning.realtimechatappbackend.userservice.userservice.dto.response.AuthenticationResponse;
import com.learning.realtimechatappbackend.userservice.userservice.dto.response.ResetPasswordResponse;
import com.learning.realtimechatappbackend.userservice.userservice.dto.response.VerifyTokenResponse;
import com.learning.realtimechatappbackend.userservice.userservice.enums.VerificationType;
import com.learning.realtimechatappbackend.userservice.userservice.model.UserAccount;
import com.learning.realtimechatappbackend.userservice.userservice.repository.AccountRepository;
import com.learning.realtimechatappbackend.userservice.userservice.repository.VerificationCodeRepository;
import com.learning.realtimechatappbackend.userservice.userservice.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private final AccountRepository userRepository;

    private final VerificationCodeRepository verificationCodeRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtTokenProvider;

    private final VerificationCodeService verificationCodeService;

    private final BaseRedisService baseRedisService;

    // Region: Register
    @Transactional
    public void sendRegisterVerificationCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        verificationCodeService.sendVerificationCode(email, VerificationType.REGISTER);
    }

    @Transactional
    public ApiResponse<AuthenticationResponse> verifyAndCreateUser(VerifyRegisterCodeRequest registerRequest) {
        var verificationOpt = verificationCodeRepository.findByEmailAndType(registerRequest.getEmail(),
                VerificationType.REGISTER);
        if (verificationOpt.isEmpty() || verificationOpt.get().getExpiresAt().isBefore(Instant.now())
                || !verificationOpt.get().getCode().equals(registerRequest.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code");
        }

        // Delete the verification code
        verificationCodeRepository.deleteByEmailAndType(registerRequest.getEmail(), VerificationType.REGISTER);

        // Create the user
        var user = new UserAccount();
        var now = Instant.now();

        user.setUserId(UUID.randomUUID().toString());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole("USER");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);

        var accessToken = jwtTokenProvider.generateAccessToken(user);
        var refreshToken = jwtTokenProvider.generateRefreshToken(user);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();

        return ApiResponse.<AuthenticationResponse>builder().success(true).message("User created successfully")
                .data(authResponse).build();
    }
    // EndRegion

    // Region: Reset Password
    @Transactional
    public void sendResetPasswordVerificationCode(String email) {
        verificationCodeService.sendVerificationCode(email, VerificationType.RESET_PASSWORD);
    }

    @Transactional
    public ApiResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        var verificationOpt = verificationCodeRepository.findByEmailAndType(resetPasswordRequest.getEmail(),
                VerificationType.RESET_PASSWORD);
        if (verificationOpt.isEmpty() || verificationOpt.get().getExpiresAt().isBefore(Instant.now())
                || !verificationOpt.get().getCode().equals(resetPasswordRequest.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code");
        }

        // Delete the verification code
        verificationCodeRepository.deleteByEmailAndType(resetPasswordRequest.getEmail(),
                VerificationType.RESET_PASSWORD);

        // Update the user's password
        var user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not found"));

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userRepository.save(user);

        return ApiResponse.<ResetPasswordResponse>builder().success(true)
                .message("Password reset successfully")
                .data(new ResetPasswordResponse(true))
                .build();
    }
    // EndRegion

    // Region: Login
    public ApiResponse<AuthenticationResponse> login(LoginRequest loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }

        var accessToken = jwtTokenProvider.generateAccessToken(user);
        var refreshToken = jwtTokenProvider.generateRefreshToken(user);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();

        return ApiResponse.<AuthenticationResponse>builder().success(true).message("Login Success").data(authResponse)
                .build();
    }
    // EndRegion

    // Region: Logout
    public ApiResponse<Void> logout(String token) {
        log.info("Processing logout request");
        
        try {
            // Remove prefix if present
            if (token == null || token.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
            }
            
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // Check if token is already blacklisted
            if (baseRedisService.exists("blacklist:" + token)) {
                log.info("Token is already blacklisted");
                return ApiResponse.<Void>builder()
                    .success(true)
                    .message("User already logged out")
                    .build();
            }
            
            // Validate token
            if (!jwtTokenProvider.validateToken(token)) {
                log.warn("Invalid token provided for logout");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
            }
            
            // Check token expiration
            var expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
            long ttlMillis = Date.from(expirationDate).getTime() - System.currentTimeMillis();
            
            if (ttlMillis <= 0) {
                log.info("Token has already expired, no need to blacklist");
                return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Token already expired")
                    .build();
            }
            
            // Calculate TTL in seconds for Redis
            long ttlSeconds = ttlMillis / 1000;
            
            // Add token to blacklist in Redis
            baseRedisService.set("blacklist:" + token, "blacklisted", ttlSeconds);
            log.info("Token has been successfully blacklisted with TTL: {} seconds", ttlSeconds);
            
            return ApiResponse.<Void>builder()
                .success(true)
                .message("Logged out successfully")
                .build();
                
        } catch (ResponseStatusException e) {
            // Re-throw existing ResponseStatusExceptions
            throw e;
        } catch (Exception e) {
            // Handle any unexpected errors
            log.error("Error during logout: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "An error occurred during logout");
        }
    }
    // EndRegion

    // Region: Refresh Token
    @Transactional
    public ApiResponse<AuthenticationResponse> refreshToken(String refreshToken) {
        try {
            log.info("Refresh Token: {}", refreshToken);

            // Validate the refresh token
            if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired refresh token");
            }

            // Extract user details from the refresh token (e.g., user ID)
            var email = jwtTokenProvider.extractUsername(refreshToken);

            log.info("Email: {}", email);

            // Check if the user exists in the database
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Generate new access and refresh tokens for the user
            var newAccessToken = jwtTokenProvider.generateAccessToken(user);
            var newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

            // Create the authentication response
            var authenticationResponse = AuthenticationResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .email(user.getEmail())
                    .userId(user.getUserId())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();

            // Return the response with new tokens
            return ApiResponse.<AuthenticationResponse>builder()
                    .success(true)
                    .message("Tokens refreshed successfully")
                    .data(authenticationResponse)
                    .build();

        } catch (ResponseStatusException e) {
            // Handle specific response status exceptions like bad request or user not found
            log.error("Error occurred while refreshing token: {}", e.getMessage());
            return ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message(e.getReason())
                    .build();
        } catch (Exception e) {
            // Handle generic exceptions
            log.error("Unexpected error occurred while refreshing token: {}", e.getMessage());
            return ApiResponse.<AuthenticationResponse>builder()
                    .success(false)
                    .message("An unexpected error occurred while refreshing token.")
                    .build();
        }
    }
    // EndRegion

    // Region: Verify Token
    public ApiResponse<VerifyTokenResponse> verifyToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        log.debug("Token: {}", token);

        try {
            boolean isBlacklisted = false;
            try {
                isBlacklisted = baseRedisService.exists("blacklist:" + token);
            } catch (Exception redisEx) {
                log.error("Redis connection error while checking blacklisted token: {}", redisEx.getMessage());
            }

            if (isBlacklisted) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has been revoked");
            }

            // Validate the token
            if (!jwtTokenProvider.validateToken(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }

            // Extract user details from token
            String email = jwtTokenProvider.extractUsername(token);

            // Look up user in database to get full details
            var user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Create response
            var response = VerifyTokenResponse.builder()
                    .valid(true)
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();

            return ApiResponse.<VerifyTokenResponse>builder()
                    .success(true)
                    .message("Token is valid")
                    .data(response)
                    .build();

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error verifying token: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error verifying token");
        }
    }
    // EndRegion
}

