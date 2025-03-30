package com.learning.realtimechatappbackend.userservice.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenRequest {
    @NotNull(message = "Refresh token cannot be null")
    private String refreshToken;
}
