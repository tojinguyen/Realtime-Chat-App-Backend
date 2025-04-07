package com.learning.realtimechatappbackend.userservice.userservice.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class VerifyTokenResponse {
    public boolean valid;
    public String userId;
    public String email;
    public String fullName;
    public String role;
}
