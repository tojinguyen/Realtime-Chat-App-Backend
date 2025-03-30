package com.learning.realtimechatappbackend.userservice.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;

    private String userId;
    private String email;
    private String fullName;
    private String role;
}

