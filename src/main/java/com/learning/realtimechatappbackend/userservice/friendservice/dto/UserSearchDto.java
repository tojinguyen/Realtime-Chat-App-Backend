package com.learning.realtimechatappbackend.userservice.friendservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDto {
    private String userId;
    private String email;
    private String fullName;
    private boolean isFriend;
    private String friendshipStatus;
}
