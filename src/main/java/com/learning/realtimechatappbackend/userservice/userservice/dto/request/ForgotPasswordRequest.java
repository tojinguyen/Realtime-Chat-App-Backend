package com.learning.realtimechatappbackend.userservice.userservice.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class ForgotPasswordRequest {
    @NotBlank(message = "Email is required")
    @NotNull(message = "Email is required")
    private String email;
}
